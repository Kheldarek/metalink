package put.ps.metalink;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import put.ps.metalink.entity.MetalinkFileInfoDetails;
import put.ps.metalink.entity.MetalinkFiles;

public class MetalinkConstructor extends Task {

    private static final String SERVER_FILES_URL = "server.files.url";
    private String url, file;
    private List<FileSet> filesets = new ArrayList<>();
    private MetalinkFiles metalinkFile = new MetalinkFiles();

    @Override
    public void execute() throws BuildException {
        super.execute();

        url = getUrl();
        filesets.forEach(this::addMetalinkFiles);
        buildMetalink();
    }

    private String getUrl() {
        if (url == null) {
            url = getProject().getProperty(SERVER_FILES_URL);
        }
        return url;
    }

    private void addMetalinkFiles(FileSet fileSet) {
        File directory = fileSet.getDir(getProject());
        String[] fileNames = getFilenames(fileSet);

        Arrays.stream(fileNames)
                .filter(Objects::nonNull)
                .forEach(file -> processFile(new File(directory, file)));
    }

    private String[] getFilenames(FileSet fileSet) {
        DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(getProject());
        return directoryScanner.getIncludedFiles();
    }

    private void processFile(File file) {
        if (file.isFile() && file.canRead()) {
            try {
                addMetalinkFiles(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addMetalinkFiles(File file) {
        if (file.isDirectory() && file.listFiles() != null) {
            Arrays.stream(file.listFiles()).forEach(this::handleFile);
        } else {
            handleFile(file);
        }
    }

    private void handleFile(File file) {
        try {
            createMetadata(file);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    private void createMetadata(File file) throws NoSuchAlgorithmException, IOException {
        if (!file.isDirectory()) {
            byte[] hash = hashify(file);

            MetalinkFileInfoDetails fileInfo = new MetalinkFileInfoDetails();
            fileInfo.name = file.getName();
            fileInfo.size = file.length();
            fileInfo.hash.value = (new HexBinaryAdapter()).marshal(hash);
            fileInfo.url = url + fileInfo.name;

            metalinkFile.add(fileInfo);
        }
    }

    private byte[] hashify(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(file.toPath()));
        return md.digest();
    }

    private void buildMetalink() {
        try {
            Marshaller marshaller = createMarshaller();
            marshaller.marshal(metalinkFile, new File(file));
        } catch (JAXBException e) {
            throw new BuildException("Incorrect Data in Metalink file!");
        }
    }

    private Marshaller createMarshaller() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(metalinkFile.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return marshaller;
    }

    public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
