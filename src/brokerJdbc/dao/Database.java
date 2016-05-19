package brokerJdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import brokerJdbc.Exception.RecordNotFoundException;
import brokerJdbc.vo.Customer;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			ConnectionManager.close(con);
		}
		
		
		
		
		
		
		return result;
	}
	
	
	 public boolean ssnExist(String ssn) {
	      boolean result = false;
	      // Customer cus = new Customer();

	      Connection con = ConnectionManager.getConnection();
	      String sql = "Select ssn from ? where ssn = ?";

	      try {
	         PreparedStatement pstat = con.prepareStatement(sql);
	         pstat.setString(1, "customer");
	         pstat.setString(2, "ssn");//TODO ssn 그냥 넣어도 되던데 난 왜 

	         ResultSet rs = pstat.executeQuery(sql);
	         result = rs.next();

	         // int row = pstat.executeUpdate();//반환값은 int이다
	         // System.out.println(row +"개 레코드 삽입");

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
	public Customer getCustomer(String ssn) throws RecordNotFoundException, SQLException{
		if(!ssnExists(ssn)) throw new RecordNotFoundException();
		Customer c = null ;
		
		
		
		return c;
		
	}
	
public static void main(String[] args) throws SQLException {
	Database db = new Database();
	db.ssnExist("111-111");
}
}

