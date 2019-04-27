package ren.gavin.export.core;

import ren.gavin.export.util.Validate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class QueryNode {

    /** 当前结点ID */
    private String id;

    /** 父节点ID */
    private String parent;

    /** SQL语句 */
    private String sqlSource;

    /** 与父节点的关联条件 */
    private String key;

    /** 子节点 */
    private List<QueryNode> children;

    /** 数据库连接 */
    private Connection connection;

    /** 结果集映射 */
    private ResultMapping resultMapping;

    public List query() throws SQLException {
        Statement statement = connection.createStatement();
        Validate.requireTrue(statement.execute(sqlSource), "未查询到结果集：" + sqlSource);

        resultMapping = new ResultMapping(id, statement.getResultSet());
        List<Map<String, Object>> result = resultMapping.getResult();
        try {
            query(result);
        } finally {
            close();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void query(List<Map<String, Object>> result) throws SQLException {
        if (null != children) {
            String bucketKey = null;
            Map<Object, Object> bucket = null;
            // key值相同，仅需要构造一次 hash桶，children做根据key值排序
            children.sort(Comparator.comparingInt(q -> q.getKey().hashCode()));
            for (QueryNode child : children) {
                String relevanceKey = child.getKey();
                Validate.requireTrue(resultMapping.hasColumnLabel(relevanceKey), child.getId() + "节点关联字段匹配失败: " + relevanceKey);
                if (Objects.equals(relevanceKey, bucketKey)) {
                    child.query(bucket);
                    continue;
                }

                bucketKey = relevanceKey;
                bucket = new HashMap<>(result.size());
                // 构造 hash桶
                for (Map<String, Object> row : result) {
                    Object key = row.get(child.getKey());
                    if (null == key) continue;

                    if (bucket.containsKey(key)) {
                        Object value = bucket.get(key);
                        if (value instanceof List) {
                            ((List<Object>)value).add(row);
                        } else {
                            List<Object> list = new ArrayList<>();
                            list.add(value);
                            list.add(row);
                            bucket.put(key, list);
                        }
                    } else {
                        bucket.put(key, row);
                    }
                }
                child.query(bucket);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void query(Map<Object, Object> bucket) throws SQLException {
        if (bucket == null || bucket.size() == 0) return;
        Statement statement = connection.createStatement();
        Validate.requireTrue(statement.execute(sqlSource), "未查询到结果集：" + sqlSource);

        List<Map<String, Object>> result = new ArrayList<>();
        resultMapping = new ResultMapping(id, statement.getResultSet());
        for (Map<String, Object> row : resultMapping) {
            result.add(row);

            Object parent = bucket.get(row.get(key));
            if (null == parent) continue;

            if (parent instanceof List) {
                for (Map<String, Object> p : (List<Map<String, Object>>) parent) {
                    relevance(p, row);
                }
            } else {
                relevance((Map<String, Object>)parent, row);
            }
        }
        query(result);
    }

    @SuppressWarnings("unchecked")
    private void relevance(Map<String, Object> parent, Map<String, Object> row) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) parent.get(id);
        if (list == null) {
            list = new ArrayList<>();
            parent.put(id, list);
        }
        list.add(row);
    }

    public void close() {
        closeResultMapping();
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    private void closeResultMapping() {
        if (null != resultMapping) {
            resultMapping.close();
        }
        if (null != children) {
            for (QueryNode child : children) {
                child.closeResultMapping();
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getSqlSource() {
        return sqlSource;
    }

    public void setSqlSource(String sqlSource) {
        this.sqlSource = sqlSource;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<QueryNode> getChildren() {
        return children;
    }

    public void setChildren(List<QueryNode> children) {
        this.children = children;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
