package net.frontlinesms.plugins.medic.data.domain.people;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;


@Entity
@DiscriminatorValue("user")
public class User extends Person {
	
	private String username;
	
	private String password;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	public enum Role{ READ(),READWRITE(),ADMIN(); 	
		public static String getRoleName(Role r){
			if(r == READ){
				return "Read Only";
			}else if(r == READWRITE){
				return "Read/Write";
			}else if(r == ADMIN){
				return "Administrator";
			}
			return null;
		}
		
		public static Role getRoleForName(String name){
			if(name == Role.getRoleName(Role.ADMIN)){
				return Role.ADMIN;
			}else if(name == Role.getRoleName(Role.READWRITE)){
				return Role.READWRITE;
			}else if(name == Role.getRoleName(Role.READ)){
				return Role.READ;
			}
			return null;
		}
	}
	
	public User(){}
	
	public User(String name, Gender gender, Date birthdate, String username, String password, Role role){
		super(name, gender, birthdate);
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public Role getRole() {
		return role;
	}
	
	public String getRoleName(){
		return Role.getRoleName(role);
	}


	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String getPersonType() {
		return "User";
	}

}
