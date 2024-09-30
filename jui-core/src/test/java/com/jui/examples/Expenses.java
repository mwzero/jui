package com.jui.examples;

import static com.jui.JuiApp.jui;
import static com.st.ST.st;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.h2.tools.DeleteDbFiles;

import com.st.DB;


public class Expenses {

	public static void main(String... args) throws Exception {
		
		populateH2();
		
		Connection  connection = DB.getConnection("org.h2.Driver","jdbc:h2:~/expensesDB", "", "");
		
		jui.table(
				"Expenses",
				st.read_sql_query("select * from expenses", connection));
		
		jui.start();
		
		//jui.set_page_config(layout="wide");
		
		// Sidebar with instructions
		/*
		st.sidebar.title("Expenses App")
		st.sidebar.write("**Overview:** This project showcases how to use Streamlit for gathering and analyzing personal expenses. It utilizes SQLite, a user-friendly database system, ideal for small-scale data management.")
		st.sidebar.write("**About the Developer:** I'm Alle Sravani, experienced Senior Data Analyst with 6+ years of experience in data analysis, data modeling, and business intelligence. Let's connect on LinkedIn: https://www.linkedin.com/in/alle-sravani/")
		st.sidebar.write("**Source Code:** You can access the code on GitHub: https://github.com/sravz3/expenses/tree/main")
		*/
	
		/*
		t_left, t_right = st.columns([3, 1])

		with t_left:
		    st.title("Personal Expenses App")
		    st.write(""" Enter your expense to be recorded into the system. """)

		with t_right:
		    # -------------- Download Button------------------ 
		    df = u.get_expenses()
		    st.download_button(
		        label="Download data as CSV",
		        data=df.to_csv().encode("utf-8"),
		        file_name="expenses.csv",
		        mime="text/csv",
		    )
		 */
		
		/*
		b_left, b_right = st.columns([3, 1])

		with b_left:
		    # ------------ Record Expense and Save -------------------- 
		    st.header("Record Expense")

		    with st.form("record_form", clear_on_submit=True):
		        st.write("Fill in your expense details here")
		        f_date = st.date_input("Enter the date", value=None)
		        f_category = st.selectbox('Pick a category', u.get_category_list())
		        f_amount = st.number_input("Enter amount in $")
		        f_submitted = st.form_submit_button('Submit Expense')

		    if f_submitted:
		        expense_result = u.save_expense(f_date,f_category,f_amount)
		        st.success(expense_result)


	with b_right:
	    st.header("Categories")
	    new_choice = st.text_input('Add your own new category:')
	    submitted = st.button("Add new category")  

	    if submitted:
	        category_result = u.save_category(new_choice)
	        st.success(category_result)
	        update_value()
	    # st.session_state['category'] = u.get_entry()

	    # ---------- Show Categories --------------------
	    st.write("Here are the categories.")
	    st.write(st.session_state.category)
		 */	    
		
	}
	
	
	public static void populateH2() throws Exception {
		
        DeleteDbFiles.execute("~", "expensesDB", true);
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:~/expensesDB");
        Statement stat = conn.createStatement();
        stat.execute("runscript from './src/test/resources/sql/expenses.sql'");
        stat.close();
        conn.close();
    }
	
}


