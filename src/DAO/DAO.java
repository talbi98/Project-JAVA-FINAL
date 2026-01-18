package DAO;

import java.sql.Connection;

import java.sql.SQLException;

import java.sql.Statement;

public abstract class DAO<T, K> {
	protected static Connection connect;
	protected Statement stmt;

	public abstract T create(T toto);

	public abstract T update(T toto);

	public abstract void delete(T toto);

	public void open() {
		connect = SingleConnection.getInstance();
		try {
			stmt = connect.createStatement();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public void close() {
		SingleConnection.close(connect);
	}

}
