package put.ps.metalink.entity;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@XmlRootElement(name = "metalink")
public class MetalinkFiles {

    public Date published = new Date();
    public List<MetalinkFileInfoDetails> file = new ArrayList<MetalinkFileInfoDetails>();
    public void add(MetalinkFileInfoDetails fileInfo) {
        file.add(fileInfo);
    }
}
