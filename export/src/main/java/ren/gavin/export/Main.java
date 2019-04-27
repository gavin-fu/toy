package ren.gavin.export;

import ren.gavin.export.core.ExportBuilder;
import ren.gavin.export.core.QueryNode;
import ren.gavin.export.util.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException, IOException {
        final String export = "sample.xml";
        InputStream resource = Resources.getResourceAsStream(export);
        ExportBuilder builder = new ExportBuilder(resource);
        QueryNode queryNode = builder.builder();
        List list = queryNode.query();
        System.out.println(list);
    }
}
