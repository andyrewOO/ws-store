package com.egfbank.tfemp.util;

import java.util.List;

public interface OracleService {
	public int executeUpdate(String sql, List<String> vs);
}
