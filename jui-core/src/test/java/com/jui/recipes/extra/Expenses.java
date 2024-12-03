package com.jui.recipes.extra;

import static com.jui.JuiApp.jui;
import static com.st.ST.st;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.h2.tools.DeleteDbFiles;

import com.jui.html.WebContainer;
import com.st.DB;


public class Expenses {

	public static void main(String... args) throws Exception {
		
		populateH2();
		
		Connection  connection = DB.getConnection("org.h2.Driver","jdbc:h2:~/expensesDB", "", "");
		
		jui.page.layout("sidebar");
		
		// Sidebar with instructions
		jui.sidebar.markdown("""
				# Expenses App
				
				**Overview:** This project showcases how to use JUI for gathering and analyzing personal expenses. It utilizes H2, a user-friendly database system, ideal for small-scale data management.
				**About the Developer:** I'm Maurizio Farina. Let's connect on LinkedIn: https://www.linkedin.com/in/farinamaurizio/
				**Source Code:** You can access the code on GitHub
				""");
		
		jui.sidebar.dropDownButton("Settings", List.of("Profile", "Account Settings", "Logout"));
		/*
		jui.columns(Map.of("left", 3, "right", 1));
		try ( JuiContainer col = jui.columns("left") ) {
			
			col.title("Personal Expenses App");
		    col.write("Enter your expense to be recorded into the system.");
			
		}
		*/
		
		try ( WebContainer col = jui.columns("right") ) {
			/*
		    col.download_button(
		        label="Download data as CSV",
		        data=df.to_csv().encode("utf-8"),
		        file_name="expenses.csv",
		        mime="text/csv",
		    )
		    */
		}
		
		jui.table(
				"Expenses",
				st.read_sql_query(connection, "select * from expenses"));
		
		jui.start();
		
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
	
	/*
	 * Gradio example
	 * 
	 * import gradio as gr

def sentence_builder(quantity, animal, countries, place, activity_list, morning):
    return f"""The {quantity} {animal}s from {" and ".join(countries)} went to the {place} where they {" and ".join(activity_list)} until the {"morning" if morning else "night"}"""

demo = gr.Interface(
    sentence_builder,
    [
        gr.Slider(2, 20, value=4, label="Count", info="Choose between 2 and 20"),
        gr.Dropdown(
            ["cat", "dog", "bird"], label="Animal", info="Will add more animals later!"
        ),
        gr.CheckboxGroup(["USA", "Japan", "Pakistan"], label="Countries", info="Where are they from?"),
        gr.Radio(["park", "zoo", "road"], label="Location", info="Where did they go?"),
        gr.Dropdown(
            ["ran", "swam", "ate", "slept"], value=["swam", "slept"], multiselect=True, label="Activity", info="Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed auctor, nisl eget ultricies aliquam, nunc nisl aliquet nunc, eget aliquam nisl nunc vel nisl."
        ),
        gr.Checkbox(label="Morning", info="Did they do it in the morning?"),
    ],
    "text",
    examples=[
        [2, "cat", ["Japan", "Pakistan"], "park", ["ate", "swam"], True],
        [4, "dog", ["Japan"], "zoo", ["ate", "swam"], False],
        [10, "bird", ["USA", "Pakistan"], "road", ["ran"], False],
        [8, "cat", ["Pakistan"], "zoo", ["ate"], True],
    ]
)

if __name__ == "__main__":
    demo.launch()
	 */
	
	
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


