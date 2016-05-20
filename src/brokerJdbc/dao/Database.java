package brokerJdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import brokerJdbc.Exception.DuplicateIDException;
import brokerJdbc.Exception.RecordNotFoundException;
import brokerJdbc.vo.Customer;
import brokerJdbc.vo.Shares;
import brokerJdbc.vo.Stock;

//업무로직을 담는 메소드
public class Database {
	/**
	 * custommer 테이블에서 SSN의 존재 유무를 확인
	 * @return 매개변수로 주어진 ssn이 존재하면 true 를 그렇지 않으면 false를 반환
	 * @param ssn 존재 유무를 확인하고자 하는 고객의 ssn
	 * javadoc 로 컴파일 하면 api document가 만들어짐. 
	 * @throws SQLException 
	 * */
	public boolean ssnExists(String ssn) throws SQLException{
		boolean result = false;
		//select 
		Connection con = ConnectionManager.getConnection();
		
		Statement stmt = null;
		//PreparedStatement pstmt = con.prepareStatement(sql);
		ResultSet sn =null;
		try {
			stmt = con.createStatement();
			//String sql = "select ssn from customer where ssn =?";
			String sql = "SELECT * FROM customer WHERE ssn = '"+ssn+"'";
			sn = stmt.executeQuery(sql);
			//ResultSet rs = 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			ConnectionManager.close(con);
		}
		
		
		
		
		
		
		return result;
	}
	
	
	 public boolean ssnExist(String ssn) {
	      boolean result = false;
	      // Customer cus = new Customer();
	      /**
	       * 커넥션 얻어오기
	       * SQL문 String에 넣기
	       * PreparedStatement 에 con.prepareStatement(sql);
	       * pstat.setString(1, ~~);
	       * int row = pstat.executeUpdate(); 
	       * 
	       */
	      Connection con = ConnectionManager.getConnection();
	      String sql = "Select ssn from customer where ssn = ?";

	      try {
	         PreparedStatement pstat = con.prepareStatement(sql);
	        // pstat.setString(1, "customer");
	         pstat.setString(1, ssn);//TODO ssn 그냥 넣어도 되던데 난 왜 

//	         ResultSet rs = pstat.executeQuery(sql);
//	         result = rs.next();
//
	          int row = pstat.executeUpdate();//반환값은 int이다
	          System.out.println(row +"개 레코드 있습니다.");
	          if(row==1){
	        	  result = true;
	          }
	      } catch (SQLException e) {
	         e.printStackTrace();
	      } finally {
	         ConnectionManager.close(con);
	      }
	      return result;

	   }
	/**
	 * 매개 변수로 주어진 ssn 에 해당하는 고객을 조회하여 반환
	 * @param ssn - 조회하고자 하는 고객의 ssn
	 * @return 조회 결과를 갖는 Customer 객체
	 * @throws RecordNotFoundException 조회하고자 하는 고객의 ssn이 존재하지 안을 경우 발생
	 * @throws SQLException 
	 */
	 /**
	  * driver 로딩
      * 커넥션 얻어오기
      * SQL문 String에 넣기
      * PreparedStatement 에 con.prepareStatement(sql);
      * pstat.setString(1, ~~);
      * int row = pstat.executeUpdate(); 
      * 
      */
	public Customer getCustomer(String ssn) throws RecordNotFoundException, SQLException{
		if(!ssnExist(ssn)) throw new RecordNotFoundException();
		Customer c = null ;
		Connection con = ConnectionManager.getConnection();
		String sql = "SELECT * FROM customer WHERE ssn = ?";
		//예외처리 
		//1. 컴파일 타임 sql, IO, Interrupted ... 4가지   
		//2. 런타임 두 가지가 있다. 런타임 컴파일러가 체크 하지 않고 사용가자 필요시 예외처리  
		
		try{
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, ssn); //****실행 전에 '?' 파라미터 지정!!!
		ResultSet rs = pstmt.executeQuery();
		//커서 최소 한 번 이동해야함.
		if(rs.next()){
			String cust_name = rs.getString("cust_name");
			String address = rs.getString("address");
			c = new Customer(ssn, cust_name , address);
		}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ConnectionManager.close(con);
		}
		return c;
	}
	/**
	 * 매개변수로 주어진 새로운 고객 정보를 등록한다.
	 * @param c 등록하고자 하는 새로운 고객 정보를 가지고 있는 Customer 객체
	 * @throws DuplicateIDException 등록하고자 하는 고객의 ssn이 이미 존재할 경우 발생
	 * 
	 * driver 로딩
     * 커넥션 얻어오기
     * SQL문 String에 넣기
     * PreparedStatement 에 con.prepareStatement(sql);
     * pstat.setString(1, ~~);
     * int row = pstat.executeUpdate(); 
     * 
     */
	public void addCustomer(Customer c)throws DuplicateIDException{
		//1. c 가 존재하면 에러 발생
		if(ssnExist(c.getSsn())) throw new DuplicateIDException();
		Connection con = ConnectionManager.getConnection();
		String sql = "INSERT INTO customer VALUES(?,?,?)";
		try{
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, c.getSsn());
		pstmt.setString(2, c.getCust_name());
		pstmt.setString(3, c.getAddress());
		int row = pstmt.executeUpdate();
		System.out.println(row +"개 레코드 삽입");
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ConnectionManager.close(con);
		}
		
	}
	/**
	 * 매개변수로 주어진 ssn에 해당하는 고객 정보 및 보유주식 정보를 삭제 
	 * 트랜잭션 처리 해줘야함. 
	 * @param ssn
	 * @throws RecordNotFoundException
	 */
	public void deleteCustomer(String ssn)throws RecordNotFoundException{
		if(!ssnExist(ssn)) throw new RecordNotFoundException();
		Connection con = ConnectionManager.getConnection();
		String sql = "DELETE FROM customer WHERE ssn = ?";
		String sql1 = "DELETE FROM shares WHERE ssn = ?";
		try{
		con.setAutoCommit(false);
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, ssn);
		pstmt.executeUpdate();
		if(true) throw new SQLException();
		PreparedStatement pstmt1 = con.prepareStatement(sql1);
		pstmt1.setString(1, ssn);
		//int row = pstmt.executeUpdate();
		int row1 = pstmt1.executeUpdate();
		//System.out.println(row+"개 레코드와 그 객체의 보유주식 "+row1+"개 삭제 됨.");
		con.commit();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ConnectionManager.close(con);
		}
	}
	/**어떤 객체를 매번 새로 생성하고 버리기보다 효율적으로 관리하기 위해
	 *  Connection 계속 만들고 버리는 것은 소모적인 작업. 
	 *  Object pool: 일정수의 객체를 미리  생성해 모아 놓고 빌려줌. 버리지 않음. 
	 * 	
	 */
	/**
	 * 매개변수로 주어진 새로운 고객정보를 갱신한다. 
	 * @param c 새로운 고객 정보를 가지고 있는 Customer 객체
	 * @throws RecordNotFoundException
	 */
	
	public void updateCustomer(Customer c)throws RecordNotFoundException{
	 if(!ssnExist(c.getSsn())) throw new RecordNotFoundException();
	 Connection con = ConnectionManager.getConnection();
	 String sql = "UPDATE customer SET cust_name = ?, address =? WHERE ssn = ?";
	 try{
	 PreparedStatement pstmt = con.prepareStatement(sql);
	 pstmt.setString(1, c.getCust_name());
	 pstmt.setString(2, c.getAddress());
	 pstmt.setString(3, c.getSsn());
	 int row = pstmt.executeUpdate();
	 System.out.println(row );
	 
	 }catch(SQLException e){
		 e.printStackTrace();
	 }finally{
		 ConnectionManager.close(con);
	 }
	}
	/**
 	 * 매개변수로 주어진 ssn의 고객이 보유하고 있는 주식의 목록을 조회하여 반환
	 * @param ssn
	 * @return
	 * @throws RecordNotFoundException
	 */
	
	public ArrayList<Shares> getPortfolio(String ssn)throws RecordNotFoundException{
		//특정 고객이 보유하고 있는 모든 주식의 정보 
		if(!ssnExist(ssn)) throw new RecordNotFoundException();
		ArrayList<Shares> list = new ArrayList<>();
		Connection con = ConnectionManager.getConnection();
		String sql ="SELECT * FROM shares WHERE ssn= ?";
		try{
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, ssn);
		ResultSet rs = pstmt.executeQuery();
		Shares sh = null;
			while (rs.next()) {
			String symbol = rs.getString("symbol");
			String quantity = rs.getString("quantity");
			 sh = new Shares(ssn, symbol, quantity);
			list.add(sh);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ConnectionManager.close(con);
		}
		
		
		return list;
	}
	
	/**
	 * Customer 테이블에 등록된 모든 고객정보를 조회한다. 
	 * @return 등록되어 있는 모든 고객정보 목록
	 */
	
	public ArrayList<Customer> getAllCustomers(){
		ArrayList<Customer> list = new ArrayList<>();
		Connection con = ConnectionManager.getConnection();
		String sql = "SELECT * FROM customer";
		try{
		PreparedStatement stmt = con.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			String ssn = rs.getString("ssn");
			String cust_name = rs.getString("cust_name");
			String address = rs.getString("address");
		Customer c = new Customer(ssn, cust_name, address);
		list.add(c);
			
		}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ConnectionManager.close(con);
		}
		
		return list;
	}
	
	public ArrayList<Stock> getAllStocks(){
		ArrayList<Stock> list = new ArrayList<>();
		Connection con = ConnectionManager.getConnection();
		String sql = "SELECT * FROM Stock";
		try{
		PreparedStatement stmt = con.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			String symbol = rs.getString("symbol");
			String price = rs.getString("price");
		Stock st = new Stock(symbol, price );
		list.add(st);
			
		}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ConnectionManager.close(con);
		}
		
		
		return list;
		
	}
	
	
public static void main(String[] args) throws SQLException, RecordNotFoundException, DuplicateIDException {
	Database db = new Database();
//	System.out.println(db.ssnExist("111-111"));
//	System.out.println(db.getCustomer("111-111"));
//	Customer c = new Customer("111-120","김진홍","여의도");
//	db.addCustomer(c);
//	db.deleteCustomer("111-120");
	
//	db.updateCustomer(c);
//	ArrayList<Shares> list =  db.getPortfolio("111-112");
//	for(Shares sh : list){
//		System.out.println(sh);
//	}
//	ArrayList<Customer> culist = db.getAllCustomers();
//	for(Customer c : culist){
//		System.out.println(c.toString());
//	}
	ArrayList<Stock> stlist = db.getAllStocks();
	for(Stock st : stlist){
		System.out.println(st.toString());
	}
	
}
}

