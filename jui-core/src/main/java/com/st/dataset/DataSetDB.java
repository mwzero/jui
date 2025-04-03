package com.st.dataset;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataSetDB extends DataSet {
	
    private Connection connection;
    private String query;

    public DataSetDB(Connection connection, String query) {
        this.connection = connection;
        this.query = query;
    }

    @Override
    public void load() throws Exception {
    	
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        ResultSetMetaData metaData = resultSet.getMetaData();

        int columnCount = metaData.getColumnCount();
        this.headers = new ArrayList<String>();

        for (int i = 1; i <= columnCount; i++) {
            headers.set(i - 1, metaData.getColumnName(i));
        }

        while (resultSet.next()) {
            List<Object> row = new ArrayList<Object>();
            for (int i = 1; i <= columnCount; i++) {
                row.set(i - 1, resultSet.getObject(i));
            }
            data.add(row);
        }
        //connection.close();
    }
}

