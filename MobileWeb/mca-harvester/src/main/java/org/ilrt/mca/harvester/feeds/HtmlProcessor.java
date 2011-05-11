package org.ilrt.mca.harvester.feeds;

import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.SimpleXmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HtmlProcessor {

    public HtmlProcessor() {

    }

    public String process(final String url, final String content) {

        String dirtyContent = "<div id='harvested-content'>" + content + "</div>";

        dirtyContent = dirtyContent.replace("<i>JISC EMBEDDED OBJECT</i>", "");

        HtmlCleaner cleaner = new HtmlCleaner();

        CleanerProperties properties = cleaner.getProperties();
        properties.setUseEmptyElementTags(false);
        properties.setOmitXmlDeclaration(true);
        properties.setOmitHtmlEnvelope(true);
        properties.setAdvancedXmlEscape(true);

        properties.setPruneTags("img,script,style");

        TagNode tagNode = cleaner.clean(dirtyContent);


        tagNode.traverse(new TagNodeVisitor() {
            public boolean visit(TagNode tagNode, HtmlNode htmlNode) {

                if (htmlNode instanceof TagNode) {
                    TagNode tag = (TagNode) htmlNode;
                    String tagName = tag.getName();

                    removeAttributes(tag);

                    if (isHeaderTag(tagName) || tagName.equals("p")) {
                        removeTagIfEmpty(tagNode, tag);
                    }

                    if (tagName.equals("b")) {
                        tag.setName("strong");
                    }

                    if (tagName.equals("i")) {
                        tag.setName("em");
                    }

                    // remove <br> tag found in <a> tags
                    if (tagName.equals("a")) {
                        TagNode[] children = tag.getChildTags();
                        if (children.length > 0) {
                            TagNode lastNode = children[children.length - 1];
                            if (lastNode.getName().equals("br")) {
                                tag.removeChild(lastNode);
                            }
                        }

                        String href = tag.getAttributeByName("href");
                        if (href != null) {
                            if (!(href.startsWith("http") || (href.startsWith("#")))) {
                                tag.setAttribute("href", url + "/" + href);
                            }
                        }
                    }
/**
                    if (tagName.equals("table")) {
                        TagNode node = handleTable(tag);
                        tagNode.replaceChild(tag, node);
                    }
**/
                    if (tagName.equals("acronym")) {
                        tag.setName("abbr");
                    }

                }

                return true;
            }
        });

        try {
            return new SimpleXmlSerializer(properties).getAsString(tagNode);
        } catch (IOException e) {
            e.printStackTrace();
            return dirtyContent;
        }

    }

    void removeTagIfEmpty(TagNode parent, TagNode tag) {

        // change non breaking space character to a space
        String temp = tag.getText().toString().replaceAll("\\u00a0", " ");

        // if its a p tag with nothing but space remove it
        if (StringUtils.isBlank(temp)) {
            parent.removeChild(tag);
        }
    }

    void removeAttributes(TagNode tag) {

        // add attributes we want to remove to a list to avoid concurrency exceptions
        Set<String> removeList = new HashSet<String>();

        // get the attribute keys
        Map<String, String> attributes = tag.getAttributes();
        Set<String> keys = attributes.keySet();

        // retain some attributes for certain tags
        for (String key : keys) {
            if (!((tag.getName().equals("a") && key.equals("href")) ||
                    (tag.getName().equals("a") && key.equals("name")) ||
                    (tag.getName().equals("acronym") && key.equals("title")) ||
                    (tag.getName().equals("div") && key.equals("id") && attributes.get(key).equals("harvested-content")))) {
                removeList.add(key);
            }
        }

        // remove what we don't want
        for (String remove : removeList) {
            tag.removeAttribute(remove);
        }
    }

    boolean isHeaderTag(String name) {
        return name.equals("h1") || name.equals("h2") || name.equals("h3") || name.equals("h4")
                || name.equals("h5") || name.equals("h6");
    }


    private TagNode handleTable(TagNode tagNode) {

        final TagNode div = new TagNode("div");
        div.setAttribute("class", "converted-table");
        //final TagNode dl = new TagNode("ul");

        tagNode.traverse(new TagNodeVisitor() {

            @Override
            public boolean visit(TagNode tagNode, HtmlNode htmlNode) {

                if (htmlNode instanceof TagNode) {

                    TagNode tag = (TagNode) htmlNode;

                    if (tag.getName().equals("tr")) {
                        tag.setName("ul");

                        List children = tag.getChildren();

                        List childrenCopy = new ArrayList();

                        for (Object o : children) {
                            if (o instanceof TagNode) {
                                TagNode t = (TagNode) o;
                                if (t.getName().equals("td") || t.getName().equals("th")) {
                                    t.setName("li");
                                    childrenCopy.add(t);

                                }
                            }
                        }
                        tag.removeAllChildren();
                        tag.addChildren(childrenCopy);
                        div.addChild(tag);
                    }

                }

                return true;
            }
        });

        return div;
    }
}
