package com.nicodev.birdyapp.model;

import java.util.List;

public class BirdyEmail {

  private String toEmail;
  private String toName;
  private String templateId;
  private String subject;
  private String headerMessage;
  private List<String> birthdayNames;
  private String unsubscribeLink;

  public String getToEmail() {
    return toEmail;
  }

  public String getToName() {
    return toName;
  }

  public String getTemplateId() {
    return templateId;
  }

  public String getSubject() {
    return subject;
  }

  public String getHeaderMessage() {
    return headerMessage;
  }

  public List<String> getBirthdayNames() {
    return birthdayNames;
  }

  public String getUnsubscribeLink() {
    return unsubscribeLink;
  }

  private BirdyEmail(Builder builder) {
    toEmail = builder.toEmail;
    toName = builder.toName;
    templateId = builder.templateId;
    subject = builder.subject;
    headerMessage = builder.headerMessage;
    birthdayNames = builder.birthdayNames;
    unsubscribeLink = builder.unsubscribeLink;
  }


  public static final class Builder {

    private String toEmail;
    private String toName;
    private String templateId;
    private String subject;
    private String headerMessage;
    private List<String> birthdayNames;
    private String unsubscribeLink;

    public Builder() {
    }

    public Builder toEmail(String val) {
      toEmail = val;
      return this;
    }

    public Builder toName(String val) {
      toName = val;
      return this;
    }

    public Builder templateId(String val) {
      templateId = val;
      return this;
    }

    public Builder subject(String val) {
      subject = val;
      return this;
    }

    public Builder headerMessage(String val) {
      headerMessage = val;
      return this;
    }

    public Builder birthdayNames(List<String> val) {
      birthdayNames = val;
      return this;
    }

    public Builder unsubscribeLink(String val) {
      unsubscribeLink = val;
      return this;
    }

    public BirdyEmail build() {
      return new BirdyEmail(this);
    }
  }
}
