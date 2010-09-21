package net.frontlinesms.plugins.patientview.flags;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.Field;
import net.frontlinesms.plugins.patientview.data.repository.FieldDao;

import org.hibernate.classic.ValidationFailure;
import org.springframework.context.ApplicationContext;

import bsh.EvalError;
import bsh.Interpreter;


public class FlagConditionValidator {
	
	private FieldDao fieldDao;
	
	public FlagConditionValidator(ApplicationContext appCon){
		this.fieldDao = (FieldDao) appCon.getBean("FieldDao");
		}
	public void validate(String condition) throws ValidationFailure{
		//replace all whitespace
		condition = condition.replaceAll("\\s", "");
		if(!condition.replaceAll("[a-zA-Z0-9.+\\-*/^<>!='\"(){}]","").equals("")){
			throw new ValidationFailure("There is a dissallowed character present in the condition");
		}
		
		//check constructions that need to be 'closed'
		//check parenthesis, single & double quotes, and curly braces
		int parensCount=0;
		int quoteCount=0;
		int curlyBraceCount=0;
		for(char c : condition.toCharArray()){
			if(c == '('){
				parensCount++;
			}else if(c == ')'){
				parensCount--;
			}else if(c == '\"'){
				quoteCount++;
			}else if(c == '{'){
				curlyBraceCount++;
			}else if(c == '}'){
				curlyBraceCount--;
			}
			if(curlyBraceCount < 0 || parensCount < 0){
				throw new ValidationFailure("You have a closing parenthesis before an opening one.");
			}
			if(curlyBraceCount > 1){
				throw new ValidationFailure("You have nested curly braces");
			}
		}
		if(parensCount != 0){
			throw new ValidationFailure("You have an unclosed parenthesis");
		}
		if(quoteCount % 2 != 0){
			throw new ValidationFailure("You have an unclosed double quote (\")");
		}
		if(curlyBraceCount != 0){
			throw new ValidationFailure("You have an unclosed curly brace");
		}
		//figure out if the supplied fields are actually fields
		Pattern p = Pattern.compile("\\{([^}]*)\\}");
		Matcher m = p.matcher(condition);
		StringBuffer testableCondition = new StringBuffer();
		while(m.find()){
			//get the field id from the matcher
			String fieldId = m.toMatchResult().group().replace("{","").replace("}", "");
			//does the field ID have any non-numeric characters in it?
			if(!fieldId.replaceAll("[^0-9]*","").equals(fieldId)){
				throw new ValidationFailure("Non-numeric characters cannot be used inside of a field identifier");
			}else{
				//attempt to retrieve the field by id
				long id = Long.parseLong(fieldId);
				Field f = fieldDao.getFieldById(id);
				if(f == null){
					throw new ValidationFailure("\""+id+"\" is not a valid field ID");
				}else{
					//if the field was successfully retrieved,
					//replace it with an example string so that we can
					//evaluate the statement and see if it works
					String replacement = "";
					if(f.getDatatype() == DataType.CHECK_BOX 
					|| f.getDatatype() == DataType.POSITIVENEGATIVE
					|| f.getDatatype() == DataType.TRUEFALSE
					|| f.getDatatype() == DataType.YESNO){
						replacement = "true";
					}else if(f.getDatatype() == DataType.NUMERIC_TEXT_FIELD){
						replacement = "100";
					}else if(f.getDatatype() == DataType.TEXT_AREA
						  || f.getDatatype() == DataType.TEXT_FIELD){
						replacement = "\"hello\"";
					}else if(f.getDatatype() == DataType.CURRENCY_FIELD 
							|| f.getDatatype() == DataType.EMAIL_FIELD
							|| f.getDatatype() == DataType.PASSWORD_FIELD
							|| f.getDatatype() == DataType.PHONE_NUMBER_FIELD
							|| f.getDatatype() == DataType.TIME_FIELD){
						replacement = "\"hello\"";
					}else if(f.getDatatype() == DataType.DATE_FIELD){
						replacement = "\"08/14/1989\"";
					}else{
						throw new ValidationFailure("The field \""+f.getLabel() +"\" cannot be used in flag conditions");
					}
					m.appendReplacement(testableCondition, replacement);
				}
			}
		}
		m.appendTail(testableCondition);
		Interpreter interpreter = new Interpreter();
		interpreter.setStrictJava(true);    
		String cond = testableCondition.toString();
		try {
		    interpreter.eval("boolean result = ("+ cond + ")");
		    System.out.println("result: "+interpreter.get("result"));
		    if(interpreter.get("result") == null){
		        throw new ValidationFailure("Eval error while parsing the condition");
		    }
		} catch (EvalError e) {
		    e.printStackTrace();
		    throw new ValidationFailure("Eval error while parsing the condition");
		}
	}
}
