package vc.dark.minecraft.reflection.mappings.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Internet implements DataParser {

    private DataParser underlying;
    private String url;

    public Internet(String url, DataParser underlying) {
        this.url = url;
        this.underlying = underlying;
    }

    public Internet(String url, Parsers parser) {
        this.url = url;
        this.underlying = parser.getParser();
    }

    public Internet(Parsers parser, String url) {
        this(url, parser);
    }

    @Override
    public void parse(String[] ignored, DataWriter out) {
        // Download data.
        String data = getData(url);
        if (data == null) {
            throw new RuntimeException("Could not download url: " + url);
        }
        String[] lines = data.split("\n");
        underlying.parse(lines, out);
    }

    private static String getData(String url2) {
        URL url = null;
        try {
            url = new URL(url2);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = url.openStream();
            byte[] byteChunk = new byte[4096];
            int n;

            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
        } catch (IOException e) {
            System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new String(baos.toByteArray());
    }
}
