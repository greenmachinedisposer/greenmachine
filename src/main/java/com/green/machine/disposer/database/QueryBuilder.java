package com.green.machine.disposer.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jurol on 3/21/2018.
 */
public class QueryBuilder {
    private final Map<String, List<Integer>> indexMap = new HashMap<>();
    private Connection connection;
    private PreparedStatement statement;
    private final String query;
    private final boolean returnGeneratedKeys;

    private QueryBuilder(Connection conn, String query, boolean returnGeneratedKeys) throws SQLException {
        this.query = query;
        this.returnGeneratedKeys = returnGeneratedKeys;

        if ( query != null ) {
            connection = conn;
            String parsedQuery = parse(query.trim(), indexMap);
            try {
                if (returnGeneratedKeys) {
                    statement = connection.prepareStatement(parsedQuery, Statement.RETURN_GENERATED_KEYS);
                } else {
                    statement = connection.prepareStatement(parsedQuery);
                }
            } catch (SQLException e) {
                connection.close();
                throw e;
            }
        }

    }

    private String parse(String query, Map<String, List<Integer>> paramMap) {
        int length = query.length();
        StringBuilder parsedQuery = new StringBuilder(length);
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        int index = 1;

        for (int i = 0; i < length; i++) {

            char c = query.charAt(i);

            // String end
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
            } else if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                }
            } else {

                // String begin
                if (c == '\'') {
                    inSingleQuote = true;
                } else if (c == '"') {
                    inDoubleQuote = true;
                } else if (c == ':' && i + 1 < length
                        && Character.isJavaIdentifierStart(query.charAt(i + 1))) {

                    // Identifier name
                    int j = i + 2;
                    while (j < length && Character.isJavaIdentifierPart(query.charAt(j))) {
                        j++;
                    }

                    String name = query.substring(i + 1, j);
                    c = '?';
                    i += name.length();
                    name = name.toLowerCase();

                    // Add to list
                    List<Integer> indexList = paramMap.get(name);
                    if (indexList == null) {
                        indexList = new LinkedList();
                        paramMap.put(name, indexList);
                    }
                    indexList.add(index);

                    index++;
                }
            }

            parsedQuery.append(c);
        }

        return parsedQuery.toString();
    }
}
