package com.green.machine.disposer.database;

import com.green.machine.disposer.utility.MiscFormatter;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.sql.Date;
import java.util.*;

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

    public static QueryBuilder create(Connection conn, String query) throws SQLException {
        return new QueryBuilder(conn, query, false);
    }

    public static QueryBuilder create(Connection conn, String query, boolean returnGeneratedKeys) throws SQLException {
        return new QueryBuilder(conn, query, returnGeneratedKeys);
    }

    private List<Integer> indexes(String name) {
        name = name.toLowerCase();
        List<Integer> result = indexMap.get(name);
        if (result == null) {
            result = new LinkedList<Integer>();
        }
        return result;
    }

    public QueryBuilder setBoolean(String name, boolean value) throws SQLException {
        for (int i : indexes(name)) {
            try {
                statement.setBoolean(i, value);
            } catch (SQLException error) {
                statement.close();
                connection.close();
                throw error;
            }
        }
        return this;
    }

    public QueryBuilder setInteger(String name, int value) throws SQLException {
        for (int i : indexes(name)) {
            try {
                statement.setInt(i, value);
            } catch (SQLException error) {
                statement.close();
                connection.close();
                throw error;
            }
        }
        return this;
    }

    public QueryBuilder setLong(String name, long value) throws SQLException {
        for (int i : indexes(name)) {
            try {
                statement.setLong(i, value);
            } catch (SQLException error) {
                statement.close();
                connection.close();
                throw error;
            }
        }
        return this;
    }

    public QueryBuilder setDouble(String name, double value) throws SQLException {
        for (int i : indexes(name)) {
            try {
                statement.setDouble(i, value);
            } catch (SQLException error) {
                statement.close();
                connection.close();
                throw error;
            }
        }
        return this;
    }

    public QueryBuilder setString(String name, String value) throws SQLException {
        for (int i : indexes(name)) {
            try {
                if (value == null) {
                    statement.setNull(i, Types.VARCHAR);
                } else {
                    statement.setString(i, value);
                }
            } catch (SQLException error) {
                statement.close();
                connection.close();
                throw error;
            }
        }
        return this;
    }

    public QueryBuilder setDate(String name, Date value) throws SQLException {
        for (int i : indexes(name)) {
            try {
                if (value == null) {
                    statement.setNull(i, Types.TIMESTAMP);
                } else {
                    statement.setTimestamp(i, new Timestamp(value.getTime()));
                }
            } catch (SQLException error) {
                statement.close();
                connection.close();
                throw error;
            }
        }
        return this;
    }

    public QueryBuilder setObject(Object object) throws SQLException {

        Method[] methods = object.getClass().getMethods();

        for (Method method : methods) {
            if (method.getName().startsWith("get") && method.getParameterTypes().length == 0) {
                String name = method.getName().substring(3);
                try {
                    if (method.getReturnType().equals(boolean.class)) {
                        setBoolean(name, (Boolean) method.invoke(object));
                    } else if (method.getReturnType().equals(int.class)) {
                        setInteger(name, (Integer) method.invoke(object));
                    } else if (method.getReturnType().equals(long.class)) {
                        setLong(name, (Long) method.invoke(object));
                    } else if (method.getReturnType().equals(double.class)) {
                        setDouble(name, (Double) method.invoke(object));
                    } else if (method.getReturnType().equals(String.class)) {
                        setString(name, (String) method.invoke(object));
                    } else if (method.getReturnType().equals(Date.class)) {
                        setDate(name, (Date) method.invoke(object));
                    }
                } catch (IllegalAccessException error) {

                } catch (  InvocationTargetException error) {

                }
            }
        }

        return this;
    }

    public <T> T executeQuerySingle(Class<T> clazz) throws SQLException {
        Collection<T> result = executeQuery(clazz);
        if ( !result.isEmpty() ) {
            return result.iterator().next();
        } else {
            return null;
        }
    }

    private <T> void addProcessors(
            List<ResultSetProcessor<T>> processors, Class<?> parameterType, final Method method, final String name) {

        if (parameterType.equals(boolean.class)) {
            processors.add(new ResultSetProcessor<T>() {

                public void process(T object, ResultSet resultSet) throws SQLException {
                    try {
                        method.invoke(object, resultSet.getBoolean(name));
                    } catch (IllegalAccessException | InvocationTargetException error) {

                    }
                }
            });
        } else if (parameterType.equals(int.class)) {
            processors.add(new ResultSetProcessor<T>() {

                public void process(T object, ResultSet resultSet) throws SQLException {
                    try {
                        method.invoke(object, resultSet.getInt(name));
                    } catch (IllegalAccessException | InvocationTargetException e) {

                    }
                }
            });
        } else if (parameterType.equals(long.class)) {
            processors.add(new ResultSetProcessor<T>() {

                public void process(T object, ResultSet resultSet) throws SQLException {
                    try {
                        method.invoke(object, resultSet.getLong(name));
                    } catch (IllegalAccessException | InvocationTargetException e) {

                    }
                }
            });
        } else if (parameterType.equals(double.class)) {
            processors.add(new ResultSetProcessor<T>() {

                public void process(T object, ResultSet resultSet) throws SQLException {
                    try {
                        method.invoke(object, resultSet.getDouble(name));
                    } catch (IllegalAccessException | InvocationTargetException e) {

                    }
                }
            });
        } else if (parameterType.equals(String.class)) {
            processors.add(new ResultSetProcessor<T>() {

                public void process(T object, ResultSet resultSet) throws SQLException {
                    try {
                        method.invoke(object, resultSet.getString(name));
                    } catch (IllegalAccessException | InvocationTargetException e ) {

                    }
                }
            });
        } else if (parameterType.equals(Date.class)) {
            processors.add(new ResultSetProcessor<T>() {

                public void process(T object, ResultSet resultSet) throws SQLException {
                    try {
                        Timestamp timestamp = resultSet.getTimestamp(name);
                        if (timestamp != null) {
                            method.invoke(object, new Date(timestamp.getTime()));
                        }
                    } catch (IllegalAccessException | InvocationTargetException  | SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (parameterType.equals(Map.class)) {
            processors.add(new ResultSetProcessor<T>() {

                public void process(T object, ResultSet resultSet) throws SQLException {
                    try {
                        JsonReader reader = Json.createReader(new StringReader(resultSet.getString(name)));
                        method.invoke(object, MiscFormatter.fromJson(reader.readObject()));
                    } catch (IllegalAccessException | InvocationTargetException | JsonParsingException e) {

                    }
                }
            });
        }
    }

    public <T> Collection<T> executeQuery(Class<T> clazz) throws SQLException {
        List<T> result = new LinkedList<T>();

        if (query != null) {
            try {
                try {
                    ResultSet resultSet = statement.executeQuery();

                    ResultSetMetaData resultMetaData = resultSet.getMetaData();

                    List<ResultSetProcessor<T>> processors = new LinkedList<ResultSetProcessor<T>>();

                    Method[] methods = clazz.getMethods();

                    for (final Method method : methods) {
                        if (method.getName().startsWith("set") && method.getParameterTypes().length == 1) {

                            final String name = method.getName().substring(3);

                            // Check if column exists
                            boolean column = false;
                            for (int i = 1; i <= resultMetaData.getColumnCount(); i++) {
                                if (name.equalsIgnoreCase(resultMetaData.getColumnLabel(i))) {
                                    column = true;
                                    break;
                                }
                            }
                            if (!column) {
                                continue;
                            }

                            addProcessors(processors, method.getParameterTypes()[0], method, name);
                        }
                    }

                    while (resultSet.next()) {
                        try {
                            T object = clazz.newInstance();
                            for (ResultSetProcessor<T> processor : processors) {
                                processor.process(object, resultSet);
                            }
                            result.add(object);
                        } catch (InstantiationException e) {
                            throw new IllegalArgumentException();
                        } catch (IllegalAccessException e) {
                            throw new IllegalArgumentException();
                        }
                    }
                } finally {

                }
            } finally {
                statement.close();
                connection.close();
            }
        }

        return result;
    }

    private interface ResultSetProcessor<T> {
        void process(T object, ResultSet resultSet) throws SQLException;
    }

    public double execute( double def ) throws SQLException {
        if ( query != null ) {
            try {
                statement.execute();

                ResultSet resultSet = statement.executeQuery();
                if ( resultSet.next() ) {
                    return resultSet.getDouble(1);
                }

            } finally {
                statement.close();
                connection.close();;
            }
        }

        return def;
    }

    public long execute(String resultLbl) throws SQLException {
        if ( query != null ) {
            try {
                statement.execute();

                ResultSet resultSet = statement.executeQuery();
                if ( resultSet.next() ) {
                    return resultSet.getLong(resultLbl);
                }

            } finally {
                statement.close();
                connection.close();
            }
        }

        return 0;
    }

    public long executeUpdate() throws SQLException {
        if (query != null) {
            try {
                statement.execute();
                if (returnGeneratedKeys) {
                    ResultSet resultSet = statement.getGeneratedKeys();
                    if (resultSet.next()) {
                        return resultSet.getLong(1);
                    }
                }
            } finally {
                statement.close();
                connection.close();
            }
        }

        return 0;
    }
}
