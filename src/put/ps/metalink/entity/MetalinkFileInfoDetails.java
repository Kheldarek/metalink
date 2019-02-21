package put.ps.metalink.entity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "file")
public class MetalinkFileInfoDetails {

    @XmlAttribute(name = "name")
    public String name;

    public long size;
    public FileInfoHash hash = new FileInfoHash();
    public String url;
}
