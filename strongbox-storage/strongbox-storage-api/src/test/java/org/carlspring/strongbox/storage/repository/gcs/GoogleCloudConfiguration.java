package org.carlspring.strongbox.storage.repository.gcs;

import org.carlspring.strongbox.storage.repository.CustomConfiguration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * @author carlspring
 */
@XmlType
@XmlRootElement(name="google-cloud-configuration")
public class GoogleCloudConfiguration
        extends CustomConfiguration
        implements Serializable
{

    @XmlAttribute
    private String bucket;

    @XmlAttribute
    private String key;


    public String getBucket()
    {
        return bucket;
    }

    public void setBucket(String bucket)
    {
        this.bucket = bucket;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

}
