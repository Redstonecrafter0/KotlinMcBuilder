package net.redstonecraft.kotlinmcbuilder.version;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

@JsonPropertyOrder({"latest", "release", "versions", "lastUpdated"})
public class Versioning {

    public String latest;
    public String release;

    @JacksonXmlElementWrapper(localName = "versions")
    @JacksonXmlProperty(localName = "version")
    public List<String> versions;

    public String lastUpdated;

}
