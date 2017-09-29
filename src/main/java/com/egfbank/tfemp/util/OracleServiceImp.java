package com.egfbank.tfemp.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class OracleServiceImp implements OracleService {
	public int executeUpdate(String sql, List<String> vs) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int rtn = 0;
		try {
			conn = OracleDBUtil.getConn();
			ps = conn.prepareStatement(sql);
			int i = 1;
			if (vs != null) {
				for (String v : vs) {
					ps.setObject(i, v);
					i = i + 1;
				}
				rtn = ps.executeUpdate();
			}
		} catch (Exception e) {
			scala.tools.jline_embedded.internal.Log
					.info("error in OracleService:" + e);
		} finally {
			OracleDBUtil.close(rs, ps, conn);
		}
		return rtn;
	}
}
