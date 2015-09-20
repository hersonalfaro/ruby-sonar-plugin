package com.godaddy.sonar.ruby.core;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.api.scan.filesystem.PathResolver.RelativePath;
import org.sonar.api.utils.WildcardPattern;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RubyFile extends Resource
{
    /**
   * 
   */
    private static final long serialVersionUID = 678217195520058883L;
    private String filename;
    private String longName;
    private String packageKey;
    private RubyPackage parent = null;

    public RubyFile(File file, List<InputFile> sourceDirs)
    {
        super();

        if (file == null)
        {
            throw new IllegalArgumentException("File cannot be null");
        }

        this.packageKey = RubyPackage.DEFAULT_PACKAGE_NAME;
        this.filename = StringUtils.substringBeforeLast(file.getName(), ".");
        this.longName = this.filename;
        String key = new StringBuilder().append(this.packageKey).append(".").append(this.filename).toString();

        if (sourceDirs != null)
        {
            PathResolver resolver = new PathResolver();
            Collection<File> colSrcDirs = toFileCollection(sourceDirs);
            RelativePath relativePath = resolver.relativePath(colSrcDirs, file);

            if (relativePath != null && relativePath.path().indexOf(File.separator) >= 0)
            {
                this.packageKey = StringUtils.substringBeforeLast(relativePath.path(), File.separator);
                this.packageKey = StringUtils.strip(this.packageKey, File.separator);
                key = this.packageKey + File.separator + this.filename;
                this.longName = key;
            }
        }

        setKey(key);
    }

    public RubyPackage getParent()
    {
        if (parent == null)
        {
            parent = new RubyPackage(packageKey);
        }
        return parent;
    }

    public String getDescription()
    {
        return null;
    }

    public Language getLanguage()
    {
        return Ruby.INSTANCE;
    }

    public String getName()
    {
        return filename;
    }

    public String getLongName()
    {
        return longName;
    }

    public String getScope()
    {
        return Scopes.FILE;
    }

    public String getQualifier()
    {
        return Qualifiers.CLASS;
    }

    public boolean matchFilePattern(String antPattern)
    {
        String patternWithoutFileSuffix = StringUtils.substringBeforeLast(antPattern, ".");
        WildcardPattern matcher = WildcardPattern.create(patternWithoutFileSuffix, File.separator);
        String key = getKey();
        return matcher.match(key);
    }

    public Collection<File> toFileCollection (List<InputFile> inputFiles){
      Collection<File> listOfFiles = new ArrayList<File>();
      for (InputFile afile : inputFiles) {
          listOfFiles.add( afile.file());
      }   
      return listOfFiles;
    }
    
    @Override
    public String toString() {
      return "\nRubyFile [getParent()=" + getParent() + ", getLanguage()=" + getLanguage() + ", getName()=" + getName()
          + ", getLongName()=" + getLongName() + ", getScope()=" + getScope() + ", getQualifier()=" + getQualifier()
          + ", getKey()=" + getKey() + ", getId()=" + getId() + ", getPath()=" + getPath() + ", getEffectiveKey()="
          + getEffectiveKey() + ", isExcluded()=" + isExcluded() + "]\n";
    }

//    @Override
//    public String toString()
//    {
//        return new ToStringBuilder(this).append("key", getKey()).append("package", packageKey).append("longName", longName).toString();
//    }

}
