package ren.gavin.export.xml;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import ren.gavin.export.util.Resources;

import java.io.IOException;

public class ExportEntryResolver implements EntityResolver {

    private static final String EXPORT_CONFIG_SYSTEM = "export-config.dtd";

    private static final String EXPORT_CONFIG_DTD = "ren/gavin/export/xml/export-config.dtd";

    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        if (null != systemId) {
            if (systemId.toLowerCase().contains(EXPORT_CONFIG_SYSTEM)) {
                try {
                    InputSource is = new InputSource(Resources.getResourceAsStream(EXPORT_CONFIG_DTD));
                    is.setPublicId(publicId);
                    is.setSystemId(systemId);
                    return is;
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return null;
    }
}
