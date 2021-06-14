package com.gb.smartcomms.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ComplexGetRequest implements GetRequest {
    private final Appliance appliance;
    private final String smartCommsTemplateId;
    private final List<PersonRecipient> personRecipients;
    private final List<OrganisationRecipient> organisationRecipients;
    private final String subject;
    private final String senderEmail;
    private final String signatureTemplateId;
    private final String notificationNumber;
    private final String caseNumber;
    private final String policyRef;
    private final String attachmentSource;
    private final String notificationTemplate;
    private final String caseTemplate;

    public ComplexGetRequest(
            final Appliance appliance,
            final String smartCommsTemplateId,
            final List<PersonRecipient> personRecipients,
            final List<OrganisationRecipient> organisationRecipients,
            final String subject,
            final String senderEmail,
            final String signatureTemplateId,
            final String notificationNumber,
            final String caseNumber,
            final String policyRef,
            final String attachmentSource) {
        this(appliance, smartCommsTemplateId, personRecipients, organisationRecipients, subject, senderEmail, signatureTemplateId, notificationNumber, caseNumber, policyRef, attachmentSource, null, null);
    }

    public ComplexGetRequest(
            final Appliance appliance,
            final String smartCommsTemplateId,
            final List<PersonRecipient> personRecipients,
            final List<OrganisationRecipient> organisationRecipients,
            final String subject,
            final String senderEmail,
            final String signatureTemplateId,
            final String notificationNumber,
            final String caseNumber,
            final String policyRef,
            final String attachmentSource,
            final String notificationTemplate,
            final String caseTemplate) {
        this.appliance = appliance;
        this.smartCommsTemplateId = smartCommsTemplateId;
        this.personRecipients = Optional.ofNullable(personRecipients).orElse(new ArrayList<>());
        this.organisationRecipients = Optional.ofNullable(organisationRecipients).orElse(new ArrayList<>());
        this.subject = subject;
        this.senderEmail = senderEmail;
        this.signatureTemplateId = signatureTemplateId;
        this.notificationNumber = notificationNumber;
        this.caseNumber = caseNumber;
        this.policyRef = policyRef;
        this.attachmentSource = attachmentSource;
        this.notificationTemplate = notificationTemplate;
        this.caseTemplate = caseTemplate;
    }

    @Override
    public Appliance getAppliance() {
        return appliance;
    }

    public String getSmartCommsTemplateId() {
        return smartCommsTemplateId;
    }

    public List<String> getRecipientsEmail() {
        return Stream.concat(getPersonRecipients().stream().map(PersonRecipient::getEmail), getOrganisationRecipients().stream().map(OrganisationRecipient::getEmail)).collect(toList());
    }

    public List<PersonRecipient> getPersonRecipients() {
        return personRecipients;
    }

    public List<OrganisationRecipient> getOrganisationRecipients() {
        return organisationRecipients;
    }

    public String getSubject() {
        return subject;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getSignatureTemplateId() {
        return signatureTemplateId;
    }

    public String getNotificationNumber() {
        return notificationNumber;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public String getPolicyRef() {
        return policyRef;
    }

    public String getAttachmentSource() {
        return attachmentSource;
    }

    public String getNotificationTemplate() {
        return notificationTemplate;
    }

    public String getCaseTemplate() {
        return caseTemplate;
    }

    public static class PersonRecipient {
        private final String customerNumber;
        private final String title;
        private final String firstName;
        private final String lastName;
        private final String phoneNumber;
        private final String mobileNumber;
        private final String email;
        private final Address address;

        public PersonRecipient(final String customerNumber, final String title, final String firstName, final String lastName, final String phoneNumber, final String mobileNumber, final String email, final Address address) {
            this.customerNumber = customerNumber;
            this.title = title;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phoneNumber = phoneNumber;
            this.mobileNumber = mobileNumber;
            this.email = email;
            this.address = address;
        }

        public String getCustomerNumber() {
            return customerNumber;
        }

        public String getTitle() {
            return title;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public String getEmail() {
            return email;
        }

        public Address getAddress() {
            return address;
        }
    }

    public static class OrganisationRecipient {
        private final String customerNumber;
        private final String shortName;
        private final String name;
        private final String abn;
        private final String bankAccount;
        private final String phoneNumber;
        private final String mobileNumber;
        private final String email;
        private final Address address;

        public OrganisationRecipient(final String customerNumber, final String shortName, final String name, final String abn, final String bankAccount, final String phoneNumber, final String mobileNumber, final String email, final Address address) {
            this.customerNumber = customerNumber;
            this.shortName = shortName;
            this.name = name;
            this.abn = abn;
            this.bankAccount = bankAccount;
            this.phoneNumber = phoneNumber;
            this.mobileNumber = mobileNumber;
            this.email = email;
            this.address = address;
        }

        public String getCustomerNumber() {
            return customerNumber;
        }

        public String getShortName() {
            return shortName;
        }

        public String getName() {
            return name;
        }

        public String getAbn() {
            return abn;
        }

        public String getBankAccount() {
            return bankAccount;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public String getEmail() {
            return email;
        }

        public Address getAddress() {
            return address;
        }
    }

    public static class Address {
        private final String streetAddress;
        private final String suburb;
        private final String state;
        private final String postCode;
        private final String country;

        public Address(final String streetAddress, final String suburb, final String state, final String postCode, final String country) {
            this.streetAddress = streetAddress;
            this.suburb = suburb;
            this.state = state;
            this.postCode = postCode;
            this.country = country;
        }

        public String getStreetAddress() {
            return streetAddress;
        }

        public String getSuburb() {
            return suburb;
        }

        public String getState() {
            return state;
        }

        public String getPostCode() {
            return postCode;
        }

        public String getCountry() {
            return country;
        }
    }
}
