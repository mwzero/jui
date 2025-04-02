package com.st.dataset;

import java.sql.*;

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
        this.headers = new String[columnCount];

        for (int i = 1; i <= columnCount; i++) {
            headers[i - 1] = metaData.getColumnName(i);
        }

        while (resultSet.next()) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = resultSet.getString(i);
            }
            data.add(row);
        }
        //connection.close();
    }
}

