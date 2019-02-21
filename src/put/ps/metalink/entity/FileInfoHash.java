package put.ps.metalink.entity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
public class FileInfoHash {

    @XmlAttribute(name = "type")
    public String type = "md5";

    @XmlValue
    public String value;
}
