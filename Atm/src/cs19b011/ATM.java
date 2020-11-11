package cs19b011;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ATM {
	
	static String miniStatement = "";
	private static Connection conn; 

	public static void main(String[] args) throws IOException{
		
		conn = connection.connection();
		System.out.println("\t \t WELCOME TO THE BRAND NEW ATM :)))");
		System.out.println();
		loginMenu();
	
	}
	
	public static void insert( int accNum, int pin, int ifsc, double cash) {// Method to insert a new account into the database
		
		String sql = "INSERT INTO users(accNum,pin,blocked,balance,ifsc) VALUES(?,?,?,?,?)";
		int blocked = 0;

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, accNum);
			stmt.setInt(2, pin);
			stmt.setInt(3, blocked);
			stmt.setDouble(4, cash);
			stmt.setInt(5, ifsc);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void loginMenu() throws IOException {// Method to display login options
		
		String a;
		Scanner in = new Scanner(System.in);
		miniStatement = "";
		
		
		
			System.out.printf("\nPress 1 to login \nPress 2 to register \nPress 3 to exit :\n ");
			a = in.nextLine();
			if (a.equals("1")) {
				login();
			} else if (a.equals("2")) {
				register();
			} else if (a.equals("3"))
				System.exit(0);
		
	}

	public static void login() throws IOException {// Method to login an existing user
		Scanner in = new Scanner(System.in);
		int accNum;
		int pin, flag =0;
		
	
		System.out.print("\nEnter your Account Number : ");
		accNum = in.nextInt();

		
	    System.out.print("Enter 5 digit pin code : ");
		pin = in.nextInt();
		
        String sql = "SELECT accNum, pin, blocked, balance FROM users";
        
        
        try (Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql))
            {
        	
        
            while (rs.next()) {
            	
               if(rs.getInt(1) == accNum && rs.getInt(2) == pin) {
            	   System.out.println("\nValidation Successfull!!!!!!\n");
            	   
            	  
            	 ATM.Menu(accNum, pin);
            	  
            	   flag = 1;
            	   break;
               }
            }
            
            if(flag == 0) {
            	System.out.println("\nYou are not a valid user. Please check your account number or PIN..\n");
            	loginMenu();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
	public static int getIFSC(int accNum) throws SQLException {//Method to get the ifsc code of an account
		
		String sql = "SELECT accNum, ifsc FROM users";
		int ifsc = 0, flag = 0;
		 
        try (Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql))
            {
        	
        
            while (rs.next()) {
            	
               if(rs.getInt(1) == accNum) {
            	   
            	   ifsc = rs.getInt("ifsc");
            	   flag = 1;
            	   break;
               }
            }
            
            if(flag == 0) {
            	System.out.println("\nNo such account exists..\n");
            	
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return ifsc;
		
		
	}
	
public static double getBalance(int accNum) throws SQLException {//Method to get the balance in an account
		
		String sql = "SELECT accNum, balance FROM users";
		int flag = 0;
		double balance = 0;
		 
		try (Statement stmt  = conn.createStatement();
	             ResultSet rs    = stmt.executeQuery(sql))
	            {
	        	
	        
	            while (rs.next()) {
	            	
	               if(rs.getInt(1) == accNum) {
	            	   
	            	   balance = rs.getDouble("balance");
	            	   flag = 1;
	            	   break;
	               }
	            }
	            
	            if(flag == 0) {
	            	System.out.println("\nNo such account exists..\n");
	            	
	            }
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
		return balance;
		
		
	}
			

		

	public static void register() throws IOException {// Method to register a new user
		
		Scanner in = new Scanner(System.in);
		registration();
		int AccNum, Pin, ifsc;
		double Balance;

		System.out.print("\nEnter your Account Number : ");
		AccNum = in.nextInt();

		System.out.println("Enter your PIN :");
		Pin = in.nextInt();
		
		System.out.println("Enter your IFSC Code : ");
		ifsc = in.nextInt();
		
		System.out.println("Enter the amount to be deposited : ");
		in.nextLine();
		Balance = in.nextDouble();
		
	
		InsertAccIntoDB app = new InsertAccIntoDB();
		insert(AccNum, Pin,ifsc, Balance);
		System.out.println("\nYOU ARE SUCCESSFULLY REGISTERED!!!\n");
		
		loginMenu();
		
	}
	
	

	public static void registration() {// Method to create a table users in the database
	
	

		try ( Statement stmt = conn.createStatement()) {
		stmt.execute("CREATE TABLE IF NOT EXISTS users(accNum integer,"
				+"pin integer," + "blocked integer," +"balance double,"+"ifsc integer,"
				 +"primary key(accNum));");
		} catch (SQLException e) {
		System.out.println(e.getMessage());
	}

}


	
	public static void Menu(int accNum, int pin) throws IOException {// Transactions Menu
		Matcher match = null;
		Scanner in = new Scanner(System.in);
		double balance = 0;
		int choice, blocked = 0;
		
		String sql1 = "SELECT * FROM users WHERE accNum ='" + accNum + "' AND pin ='" + pin + "';";

		try (
				Statement stmt = conn.createStatement();
				 ResultSet rs = stmt.executeQuery(sql1)) {

			while (rs.next()) {
				
				blocked = rs.getInt(3);
				balance = rs.getDouble(4);
				
				
				}
			
		while(true) {
				System.out.print(
						"\nPress 1 to check current balance \nPress 2 to withdraw cash\nPress 3 to change pin \nPress 4 for to deposit cash\nPress 5 to transfer amount into another account\nPress 6 for Mini Statement\nPress 7 to block your account\nPress 8 to unblock your account\nPress 9 to go back to Main Menu\nPress 10 to exit :  ");
				choice = in.nextInt();

				if (choice == 1) {
					if(blocked == 1) {
						System.out.println("\nYour account is blocked...Ublock to proceed transactions");
						Menu(accNum, pin);
					}else {
						
						File file = new File("./current.txt");
						file.createNewFile();
						FileWriter fr = new FileWriter(file);

						try {

							
							fr.write("\nYour current balance is : " + balance + "\n");
							fr.close();

						} catch (Exception e) {
							e.printStackTrace();
						}

						BufferedReader reader;

						try {

							reader = new BufferedReader(new FileReader(file));
							String line = reader.readLine();
							while (line != null) {
								System.out.println(line);
								line = reader.readLine();
							}
							System.out.println();
							reader.close();

						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}

					
				}
				if (choice == 2) {
					Double withdrawcash, newamount;
					if(blocked == 1) {
						System.out.println("\nYour account is blocked...Unblock to proceed transactions.");
						Menu(accNum, pin);
					}else {
						System.out.println("\nHow much cash you want to withdraw : ");
						double withdraw = in.nextDouble();
						
						if(balance >= withdraw) {
							
							int otp = generateOTP();
							System.out.println("\nYour One Time Password for cash withdrawal is : "+otp);
							System.out.println("Enter the OTP sent to you in order to withdraw cash : ");
							int enteredOtp = in.nextInt();
							
							if(enteredOtp == otp) {
								
								System.out.println("\nOTP verification successful!!!!!");
								withdrawcash = balance;
								newamount = withdrawcash - withdraw;

								File file = new File("./withdraw.txt");
								file.createNewFile();
								FileWriter fr = new FileWriter(file);

								try {

									
									fr.write("Amount-deducted : " + withdraw + "\n");
									fr.write("New-Balance : " + newamount + "\n");
									fr.close();
									balance = newamount;
									String sql2 = "UPDATE users SET balance ='" + balance + "' " + "WHERE accNum ='" + accNum + "';";

									try (
											PreparedStatement pstmt = conn.prepareStatement(sql2)) {

										pstmt.executeUpdate();
									} catch (SQLException e) {
										System.out.println(e.getMessage());
									}

								} catch (Exception e) {
									e.printStackTrace();
								}

								BufferedReader reader;

								try {

									reader = new BufferedReader(new FileReader(file));
									String line = reader.readLine();
									while (line != null) {
										System.out.println(line);
										line = reader.readLine();
									}
									System.out.println();
									reader.close();

								} catch (Exception e) {
									e.printStackTrace();
								}
								
								miniStatement += "\n" + withdraw + "/- rupees withdrawn.";

							}else {
								System.out.println("Incorrect OTP...");
								
							}

							
						}else {
							System.out.println("\nYour account has no sufficient balance for withdrawal..\n");
						}
						
					
						
					}
						
				}
				
				if (choice == 3) {
					
					if(blocked == 1) {
						System.out.println("\nYour account is blocked...Unblock to proceed transactions.");
						Menu(accNum, pin);
					}else {
						
						int newpin = 0;
						System.out.println("\nEnter new pin : ");
						newpin = in.nextInt();
						
						String sql3 = "UPDATE users SET pin ='" + newpin + "' " + "WHERE accNum ='" + accNum + "';";

						try (
							PreparedStatement pstmt2 = conn.prepareStatement(sql3)) {
							pstmt2.executeUpdate();
						} catch (SQLException e) {
							System.out.println(e.getMessage());
						}
						System.out.println("\nPIN CODE UPDATED SUCCESSFULLY!!!!");
						
					}
					
					
				}
				
				if (choice == 4) {
					
					if(blocked == 1) {
						System.out.println("\nYour account is blocked...Unblock to proceed transactions.");
						Menu(accNum, pin);
						
					}else {
						

						Double depositcash, newAmount;
						System.out.println("\nHow much cash you want to deposit : ");
						double deposit = in.nextDouble();
						
								depositcash = balance;
								newAmount = depositcash + deposit;

								File file = new File("./deposit.txt");
								file.createNewFile();
								FileWriter fr = new FileWriter(file);

								try {

									
									fr.write("Amount-deposited : " + deposit + "\n");
									fr.write("New-Balance : " + newAmount + "\n");
									fr.close();
									balance = newAmount;
									String sql3 = "UPDATE users SET balance ='" + balance + "' " + "WHERE accNum ='" + accNum + "';";

									try (
											PreparedStatement pstmt2 = conn.prepareStatement(sql3)) {

										pstmt2.executeUpdate();
									} catch (SQLException e) {
										System.out.println(e.getMessage());
									}

								} catch (Exception e) {
									e.printStackTrace();
								}

								BufferedReader reader;

								try {

									reader = new BufferedReader(new FileReader(file));
									String line = reader.readLine();
									while (line != null) {
										System.out.println(line);
										line = reader.readLine();
									}
									System.out.println();
									reader.close();

								} catch (Exception e) {
									e.printStackTrace();
								}
								miniStatement += "\n" + deposit + "/- rupees deposited.";

						
						
					}
					
					}
				
				if(choice == 5) {
					
					if(blocked == 1) {
						System.out.println("\nYour account is blocked...Unblock to proceed transactions.");
						Menu(accNum, pin);
					}else {
						System.out.println("\nEnter the account number into which you want to tansfer money : ");
						int acc = in.nextInt();
						System.out.println("Enter the IFSC Code : ");
						int enteredIfsc = in.nextInt();
						int correctIfsc = getIFSC(acc);
						if(enteredIfsc == correctIfsc) {
							
							System.out.println("\nIFSC verification successfull\nEnter the amount to be transferred : ");
							double tran = in.nextDouble();
							if(balance >= tran) {
								
								balance = balance - tran;
								File file = new File("./transfer.txt");
								file.createNewFile();
								FileWriter fr = new FileWriter(file);

								try {

									
									fr.write("\nAmount-transferred : " + tran );
									fr.write("\nNew-Balance : " + balance + "\n");
									fr.close();
									String sql4 = "UPDATE users SET balance ='" + balance + "' " + "WHERE accNum ='" + accNum + "';";

									try (
											PreparedStatement pstmt2 = conn.prepareStatement(sql4)) {

										pstmt2.executeUpdate();
									} catch (SQLException e) {
										System.out.println(e.getMessage());
									}

								} catch (Exception e) {
									e.printStackTrace();
								}

								BufferedReader reader;

								try {

									reader = new BufferedReader(new FileReader(file));
									String line = reader.readLine();
									while (line != null) {
										System.out.println(line);
										line = reader.readLine();
									}
									System.out.println();
									reader.close();

								} catch (Exception e) {
									e.printStackTrace();
								}
								miniStatement += "\n" + tran + "/- rupees transferred.";
								
								double bal = getBalance(acc);
								bal = bal + tran;
								
									String sql5 = "UPDATE users SET balance ='" + bal + "' " + "WHERE accNum ='" + acc + "';";

									try (
											PreparedStatement pstmt3 = conn.prepareStatement(sql5)) {

										pstmt3.executeUpdate();
									} catch (SQLException e) {
										System.out.println(e.getMessage());
									}

								}else {
									System.out.println("Your account has not enough money to perform the transaction");
									Menu(accNum, pin);
									}
							
									
								
							}else {
								System.out.println("Incorrect IFSC Code : ");
								Menu(accNum, pin);
							}
						}
					}
				
				if(choice == 6) {
					
					if(blocked == 1) {
						System.out.println("\nYour account is blocked...Unblock to proceed transactions.");
						Menu(accNum, pin);
						
					}else {
						
						if(miniStatement != null) {
							
							System.out.println("\nTransactions ::");
							System.out.println(miniStatement);
							
						}else {
							System.out.println("No transactions done.");
						}
						
					}
					
				}
				
				if(choice == 7) {
					
					int blockedStatus = 1;
					
					 String sql4 = "UPDATE users SET blocked ='" + blockedStatus + "' " + "WHERE accNum ='" + accNum + "';";

				        try (
				                PreparedStatement pstmt3 = conn.prepareStatement(sql4)) {

				            pstmt3.executeUpdate();

				        } catch (SQLException e) {
				            System.out.println(e.getMessage());
				        }
				        System.out.println("\nYour account is blocked!!");
				       loginMenu();
					
				}
				
				if(choice == 8) {
					
					int blockedStatus = 0;
					
					 String sql4 = "UPDATE users SET blocked ='" + blockedStatus + "' " + "WHERE accNum ='" + accNum + "';";

				        try (
				                PreparedStatement pstmt3 = conn.prepareStatement(sql4)) {

				            pstmt3.executeUpdate();

				        } catch (SQLException e) {
				            System.out.println(e.getMessage());
				        }
				        System.out.println("\nYour account is unblocked!!");
				       loginMenu();
					
				}
				
			
				
				if (choice == 9) {
					loginMenu();
				}
				
				if(choice == 10) {
					System.exit(0);
				}
				}}catch (SQLException e) {
					System.out.println(e.getMessage());
				}
		}
		 

	public static int generateOTP() {
		
		Random rand = new Random();
	     
	    int otp = rand.nextInt((9999 - 100) + 1) + 10;
	    
	    return otp;
	        
		
	}

}


