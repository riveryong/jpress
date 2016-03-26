package io.jpress.install;

import io.jpress.core.dialect.DbDialect;
import io.jpress.core.dialect.DbDialectFactory;
import io.jpress.utils.DateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.druid.DruidPlugin;

public class InstallUtils {

	private static String dbHost;
	private static String dbName;
	private static String dbUser;
	private static String dbPassword;
	public static String dbTablePrefix;

	public static DbDialect mDialect;

	public static void init(String db_host, String db_name, String db_user,
			String db_password, String db_tablePrefix) {
		dbHost = db_host;
		dbName = db_name;
		dbUser = db_user;
		dbPassword = db_password;
		dbTablePrefix = db_tablePrefix;
		mDialect = DbDialectFactory.getDbDialect();
	}

	public static boolean createDbProperties() {
		Properties p = new Properties();
		p.put("db_host", dbHost);
		p.put("db_name", dbName);
		p.put("db_user", dbUser);
		p.put("db_password", dbPassword);
		p.put("db_tablePrefix", dbTablePrefix);
		File pFile = new File(PathKit.getRootClassPath(), "db.properties");
		return save(p, pFile);
	}

	public static boolean createJpressProperties() {
		Properties p = new Properties();
		p.put("devMode", "false");
		p.put("cookie_encrypt_key", UUID.randomUUID().toString());
		File pFile = new File(PathKit.getRootClassPath(), "jpress.properties");
		return save(p, pFile);
	}

	private static boolean save(Properties p, File pFile) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(pFile);
			p.store(fos, "Auto create by JPress");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
				}
		}
		return true;
	}

	public static List<String> getTableList() throws SQLException {
		DruidPlugin dp = createDruidPlugin();
		Connection conn = dp.getDataSource().getConnection();
		List<String> tableList = query(conn, mDialect.forShowTable());
		conn.close();
		dp.stop();
		return tableList;
	}


	public static void createJpressDatabase() throws SQLException {
		String installSql = mDialect.forInstall(dbTablePrefix);
		DruidPlugin dp = createDruidPlugin();
		Connection conn = dp.getDataSource().getConnection();
		executeBatchSql(conn, installSql);
		conn.close();
		dp.stop();

	}

	public static void setWebName(String webName) throws SQLException {

		executeSQL(mDialect.forInsertWebName(dbTablePrefix),webName);
	}

	public static void setWebFirstUser(String username, String password, String salt) throws SQLException {

		executeSQL(mDialect.forInsertFirstUser(dbTablePrefix),
				username, password, salt, "administrator", "activited",
				DateUtils.now());
	}

	public static void executeSQL(String sql, Object... params) throws SQLException {
		DruidPlugin dp = createDruidPlugin();
		Connection conn = dp.getDataSource().getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			if (null != params && params.length > 0) {
				int i = 0;
				for (Object param : params) {
					pstmt.setString(++i, param.toString());
				}
			}
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pstmt.close();
			conn.close();
			dp.stop();
		}
	}

	private static void executeBatchSql(Connection conn, String batchSql) throws SQLException {
		Statement pst = conn.createStatement();
		if (null == batchSql) {
			throw new SQLException("SQL IS NULL");
		}

		if (batchSql.contains(";")) {
			String sqls[] = batchSql.split(";");
			if (null != sqls && sqls.length > 0) {
				for (String sql : sqls) {
					if (null != sql && !"".equals(sql.trim()))
						pst.addBatch(sql);
				}
			}
		} else {
			pst.addBatch(batchSql);
		}
		pst.executeBatch();
		close(pst);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T> List<T> query(Connection conn, String sql)
			throws SQLException {
		List result = new ArrayList();
		PreparedStatement pst = conn.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		int colAmount = rs.getMetaData().getColumnCount();
		if (colAmount > 1) {
			while (rs.next()) {
				Object[] temp = new Object[colAmount];
				for (int i = 0; i < colAmount; i++) {
					temp[i] = rs.getObject(i + 1);
				}
				result.add(temp);
			}
		} else if (colAmount == 1) {
			while (rs.next()) {
				result.add(rs.getObject(1));
			}
		}
		close(rs, pst);
		return result;
	}

	private static final void close(ResultSet rs, Statement st) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {}
		}
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {}
		}
	}

	private static final void close(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {}
		}
	}

	private static DruidPlugin createDruidPlugin() {
		DruidPlugin plugin = mDialect.createDuidPlugin(dbHost, 
				dbName, 
				dbUser,
				dbPassword);

		plugin.start();

		return plugin;
	}

}
