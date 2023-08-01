import java.util.*;
import java.sql.*;

abstract class newUser{
	abstract public void insert()throws Exception;
	
}
class Create extends newUser{
	private String name;
	private String pwd;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public void insert() throws Exception {
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/commerce", "root", "Fevin1908*");
	String q2="insert into users(user_name,password) values(?,?)";
	PreparedStatement st=con.prepareStatement(q2);
	st.setString(1, name);
	st.setString(2, pwd);
	st.executeUpdate();
    con.close();
	}

}

public class App {
    private static String username=null;
    private static String password=null;
    private static Scanner sc=new Scanner( System.in);

    public static void payment(int id,Connection con) throws Exception{
        System.out.print("Proceed to payment (y) or cancel order(n)? ");
        char val=sc.next().charAt(0);
        if(val=='y'){
            System.out.print("Enter Product id: ");
            int prod_id = sc.nextInt();
            String sqlUpdate = "UPDATE orders SET payment_status = ? WHERE payment_status = 'Pending' and product_id = ? and user_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sqlUpdate)) {
            pstmt.setString(1, "Success");
            pstmt.setInt(2, prod_id);
            pstmt.setInt(3, id);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Payment successful!");
                
                display(con, id);
            } else {
                System.out.println("No records were updated. Please check the product_id.");
            }
        }
        }else if(val=='n'){
            updateOrder(con, id);
        }
        
        else{
            display(con, id);
        }
    }

    public static void checkOrders(Connection con,int userId,String status) throws Exception{
        System.out.println("CART\n");
        String query = "SELECT * FROM orders WHERE user_id = ? and payment_status=? order by payment_status";
        int cnt=0;
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Order Id\tProduct Id\tProduct name\tQuantity\tStatus");
            System.out.println("------------------------------------------------------------------------");
            while(rs.next()){

                if(rs.getString(3).length()>=9)
                        System.out.println(rs.getInt(1) + "\t\t" + rs.getString(2) + "\t\t" + rs.getString(3)+"\t"+rs.getString(5)+"\t\t"+rs.getString(7));
                    else    
                        System.out.println(rs.getInt(1) + "\t\t" + rs.getString(2) + "\t\t" + rs.getString(3)+"\t\t"+rs.getString(5)+"\t\t"+rs.getString(7));               cnt++;
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        if(cnt==0){
            System.out.print("No pending orders !\nGo to main page (y/n) ? ");
            if(sc.next().charAt(0)=='y'){
                display(con, userId);
            }else{
                showproducts(con, userId);
            }
        }else{
            payment(userId, con);
        }
    }

    public static void checkOrders(Connection con, int userId) throws Exception {
        System.out.println();
        String query = "SELECT * FROM orders WHERE user_id = ? order by payment_status";
        System.out.println("ORDER HISTORY\n---------------");
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            int cnt=0;
            int scnt=0;
            try {
                while (rs.next()) {
                    if(rs.getString(7).equals("Pending")){
                        cnt++;
                    }else if(rs.getString(7).equals("Success")){
                        scnt++;
                    }
                    if(cnt==1){
                        System.out.println("Pending");
                        System.out.println("-------------------------------------------------------------------------");
                        System.out.println("Order Id\tProduct Id\tProduct name\tQuantity\tStatus");
                        System.out.println("-------------------------------------------------------------------------");
                        cnt=20;
                    }
                    if(scnt==1){
                        System.out.println("Completed");
                        System.out.println("-------------------------------------------------------------------------");
                        System.out.println("Order Id\tProduct Id\tProduct name\tQuantity\tStatus");
                        System.out.println("-------------------------------------------------------------------------");
                        scnt=20;
                    }
                    if(rs.getString(3).length()>=9)
                        System.out.println(rs.getInt(1) + "\t\t" + rs.getString(2) + "\t\t" + rs.getString(3)+"\t"+rs.getString(5)+"\t\t"+rs.getString(7));
                    else    
                        System.out.println(rs.getInt(1) + "\t\t" + rs.getString(2) + "\t\t" + rs.getString(3)+"\t\t"+rs.getString(5)+"\t\t"+rs.getString(7));

                }
                // cnt=scnt=0;
                // if(cnt>0)
                //     payment(userId, con);
                // else{
                    // System.out.println("No new orders!");
                    System.out.println("Enter 'y' to go to the main page or any other key to show products");
                    char val = sc.next().charAt(0);
                    if (val == 'y') {
                        display(con, userId);
                    } else {
                        showproducts(con, userId);
                    }
                // }
                    
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
            // cnt=scnt=0;
        }
    }


    public static void updateOrder(Connection con,int UserId) throws Exception{
        System.out.print("Enter product ID: ");
        int prod_id=sc.nextInt();
        String query="delete from orders where product_id=? and user_id=?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, prod_id);
            pstmt.setInt(2, UserId);
            pstmt.executeUpdate();
            System.out.println("Order Cancelled Successfully!");
            display(con, UserId);
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }

    }
    



    public static void showproducts(Connection con,int Userid) throws Exception{
        
        String query="select * from products";
        Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = st.executeQuery(query);
        while(rs.next()){
            System.out.println("Product Id\tProduct name");
            System.out.println("----------\t------------");
            System.out.println(rs.getInt(1)+"\t\t"+rs.getString(2));
            System.out.println("Description: "+ rs.getString(3));
            System.out.println("Price:\tRs."+ rs.getInt(4)+"/-");
            System.out.println();
        }
        System.out.println("_________________________________________________________");

        

        System.out.println("Buy product(y) or Go to main page(n): ");
        if(sc.next().charAt(0)=='y'){
            System.out.print("Enter product ID to buy: ");
            int prod_id=sc.nextInt();
            System.out.print("Enter quantity: ");
            int quantity=sc.nextInt();
            String sqlInsert = "INSERT INTO orders (product_id,product_name,price,quantity,user_id) VALUES (?,?,?,?,?)";
            PreparedStatement pstmt = con.prepareStatement(sqlInsert);
            pstmt.setInt(1, prod_id);
            rs.beforeFirst();
            while(rs.next()){
                try{
    
                    if(prod_id==rs.getInt(1)){
                        pstmt.setString(2, rs.getString(2));
                        pstmt.setInt(3, rs.getInt(4));
                        break;
                    }
                }catch(Exception e){
                    System.out.println("Something happened ! Try again :)");
                }
    
            }
            pstmt.setInt(4, quantity);
            pstmt.setInt(5, Userid);
            int rowsInserted = pstmt.executeUpdate();
            if(rowsInserted>0){
                System.out.println("Order added successfully !!");
                rs.beforeFirst();
                display(con, Userid);
            }else{
                System.out.println("Please try again");
            }
        }else{
            display(con, Userid);
        }
    }



    public static void display(Connection con,int id) throws Exception{
        System.out.println("View products\t  :\tEnter 1");
        System.out.println("View order history:\tEnter 2");
        System.out.println("View cart\t  :\tEnter 3");
        System.out.println("Exit \t\t  :\tEnter 4\n------------------------");
        try{
            int val=sc.nextInt();
            if(val==1){
                showproducts(con,id);
            }else if(val==2){
                checkOrders(con,id);
            }else if(val==3){
                checkOrders(con, id, "Pending");
            }
            else if(val==4){
                System.out.println("Thank You !!");
                System.exit(0);
            }
        }catch(Exception e){
            System.out.println("Invalid Input :(");
            display(con, id);
        }

    }



    public static void main(String args[]) throws Exception {
        
        System.out.println("------------------------------------\nECOMMERCE ORDER MANAGEMENT SYSTEM\n------------------------------------\n\n");
        System.out.println("Login (l) or SignUp? (s)");
        Create create=new Create();
        if(sc.next().charAt(0)=='s'){
            sc.nextLine();
            // Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/commerce", "root", "Fevin1908*");
            System.out.print("Enter Username: ");
            String nuser=sc.nextLine();
            create.setName(nuser);
            System.out.print("Enter Password: ");
            String pwd=sc.nextLine();
            create.setPwd(pwd);
            create.insert();
            System.out.println("User created ");
        }
        
        System.out.println("LOGIN");
        sc.nextLine();
        System.out.println("****************************");
        System.out.print("Enter Username: ");
        username=sc.nextLine();
        System.out.print("Enter Password: ");
        password=sc.nextLine();
        
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/commerce", "root", "Fevin1908*");

        System.out.println("****************************");
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("select * from users");
        // try{
            while (rs.next()) {
                if(password.equals(rs.getString(3)) && username.equals(rs.getString(2))){
                    System.out.println("Welcome again "+ rs.getString(2).toUpperCase()+" !");
                    System.out.println("________________________");
                    display(con,rs.getInt(1));
                    break;
                }
            }
            // }catch(Exception e){
                //     System.out.println("Thank You !");
                //     System.exit(0);
                // }
        rs.close();
        con.close();
        sc.close(); 
    }
}
