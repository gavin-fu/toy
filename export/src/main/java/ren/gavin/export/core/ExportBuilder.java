package ren.gavin.export.core;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ren.gavin.export.util.DBUtil;
import ren.gavin.export.util.Validate;
import ren.gavin.export.util.XMLDomParser;
import ren.gavin.export.util.parser.GenericTokenParser;
import ren.gavin.export.util.parser.PropertyTokenHandler;
import ren.gavin.export.xml.ExportEntryResolver;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class ExportBuilder {

    private XMLDomParser xmlParser;

    private GenericTokenParser tokenParser;

    private Connection connection;

    public ExportBuilder(InputStream inputStream) {
        this(inputStream, null);
    }

    public ExportBuilder(InputStream inputStream, Properties properties) {
        commonStructure(new XMLDomParser.Builder().setResource(inputStream), properties);
    }

    public ExportBuilder(Reader reader) {
        this(reader, null);
    }

    public ExportBuilder(Reader reader, Properties properties) {
        commonStructure(new XMLDomParser.Builder().setResource(reader), properties);
    }

    private void commonStructure(XMLDomParser.Builder builder, Properties properties) {
        xmlParser = builder
                .setValidation(true)
                .setIgnoringElementContentWhitespace(true)
                .setEntityResolver(new ExportEntryResolver())
                .builder();
        tokenParser = new GenericTokenParser("${", "}", new PropertyTokenHandler(properties));
        try {
            connection = DBUtil.getConnection();
            connection.setReadOnly(true);
        } catch (SQLException e) {
            throw new ExportException("获取数据库连接失败", e);
        }
    }

    public QueryNode builder() {
        Node export = xmlParser.evaluateNode("export");
        Validate.requireNonNull(export);
        String root = getNodeAttribute(export, "root");
        Validate.requireNonEmpty(root);
        NodeList selectList = xmlParser.evaluateNodes(export, "select");
        Validate.requireNonEmpty(selectList);

        Map<String, QueryNode> map = new HashMap<>();
        int length = selectList.getLength();
        for (int i = 0; i < length; i++) {
            QueryNode queryNode = createQueryNode(selectList.item(i));
            Validate.requireNonEmpty(queryNode.getId());
            QueryNode absent = map.put(queryNode.getId(), queryNode);
        }

        for (Map.Entry<String, QueryNode> entry : map.entrySet()) {
            QueryNode value = entry.getValue();
            String parent = value.getParent();
            if (null != parent && ! "".equals(parent)) {
                QueryNode parentNode = map.get(parent);
                Validate.requireNonNull(parentNode, "未获取到到当前节点父节点: " + value.getId());
                Validate.requireNonEmpty(value.getKey(), "当前结点关联字段不能为空");

                List<QueryNode> children = parentNode.getChildren();
                if (null == children) {
                    children = new ArrayList<>();
                    parentNode.setChildren(children);
                }
                children.add(value);
            } else {
                if (!root.equals(value.getId())) {
                    throw new ExportException("当前 select结点未指定父节点: " + value.getId());
                }
            }
        }

        return map.get(root);
    }

    private QueryNode createQueryNode(Node node) {
        String id = getNodeAttribute(node, "id");
        Validate.requireNonEmpty(id);
        String sql = node.getTextContent().trim();
        Validate.requireNonEmpty(sql);

        QueryNode queryNode = new QueryNode();
        queryNode.setId(id);
        queryNode.setSqlSource(tokenParser.parser(sql));
        queryNode.setParent(getNodeAttribute(node, "parent"));
        queryNode.setKey(getNodeAttribute(node, "key"));
        queryNode.setConnection(connection);
        return queryNode;
    }

    private String getNodeAttribute(Node node, String key) {
        NamedNodeMap attributes = node.getAttributes();
        Node value = attributes.getNamedItem(key);
        return value == null ? "" : value.getTextContent().trim().toUpperCase();
    }
}
