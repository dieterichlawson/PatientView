package net.frontlinesms.plugins.patientview.search;

import java.util.List;

public abstract class PagedResultSet {
	
	//paging stuff
	/**
	 * The total number of results in the result set
	 * starts counting from 1
	 */
	protected int totalResults=0;
	
	/**
	 * The current page in the result set
	 * starts counting from 0
	 */
	protected int currentPage=0;
	
	/**
	 * total pages in the result set
	 * starts counting from 1
	 * calculated by taking totalResults/pageSize
	 */
	protected int totalPages=0;
	
	/**
	 * the size of the pages
	 */
	protected int pageSize=30;
	
	public int getCurrentPage() {
		return currentPage;
	}
	
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	protected void setTotalResults(int totalResults){
		this.totalResults = totalResults;
		if(totalResults % pageSize == 0){
			totalPages = totalResults / pageSize;
		}else{
			totalPages = (totalResults / pageSize) +1;
		}
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getTotalResults() {
		return totalResults;
	}
	
	public int getTotalPages() {
		return totalPages;
	}
	
	public void nextPage(){
		if(currentPage < (totalPages - 1)){
			currentPage++;
			refresh();
		}
	}
	
	public void previousPage(){
		if(currentPage > 0){
			currentPage--;
			refresh();
		}
	}
	
	public int getFirstResultOnPage(){
		return currentPage * pageSize + 1;
	}
	
	public int getLastResultOnPage(){
		if(currentPage < totalPages-1){
			return currentPage * pageSize + pageSize;
		}else{
			return totalResults;
		}
	}
	
	public boolean hasNextPage(){
		return currentPage < (totalPages-1);
	}
	
	public boolean hasPreviousPage(){
		return currentPage > 0;
	}

	public void resetPaging(){
		currentPage=0;
	}
	
	public abstract void refresh();
	
	public abstract List getFreshResultsPage();
	
	public abstract List getResultsPage();
}
