package com.appify.jaedgroup.model;

public class EstateTransaction {
    private String id;
    private String name;
    private String estateId;
    private String estateName;
    private String phoneNo;
    private String customerAddress;
    private String dateOfBirth;
    private String occupation;
    private String maritalStatus;
    private String nextOfKin;
    private String nextOfKinRelationship;
    private String nextOfKinPhoneNo;
    private String nextOfKinAddress;
    private String purposeOfLand;
    private int amountPaid;
    private boolean willAcceptAlternatePlot;
    private String estateType;
    private String datePaid;
    private String reference;
    private String email;
    private String userId;
    private String transactionStatus;

    private static EstateTransaction ourInstance = new EstateTransaction();

    public static EstateTransaction getInstance() {
        return ourInstance;
    }

    private EstateTransaction() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEstateId() {
        return estateId;
    }

    public void setEstateId(String estateId) {
        this.estateId = estateId;
    }

    public String getEstateName() {
        return estateName;
    }

    public void setEstateName(String estateName) {
        this.estateName = estateName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(int amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getEstateType() {
        return estateType;
    }

    public void setEstateType(String estateType) {
        this.estateType = estateType;
    }

    public String getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(String datePaid) {
        this.datePaid = datePaid;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getNextOfKin() {
        return nextOfKin;
    }

    public void setNextOfKin(String nextOfKin) {
        this.nextOfKin = nextOfKin;
    }

    public String getNextOfKinRelationship() {
        return nextOfKinRelationship;
    }

    public void setNextOfKinRelationship(String nextOfKinRelationship) {
        this.nextOfKinRelationship = nextOfKinRelationship;
    }

    public String getNextOfKinPhoneNo() {
        return nextOfKinPhoneNo;
    }

    public void setNextOfKinPhoneNo(String nextOfKinPhoneNo) {
        this.nextOfKinPhoneNo = nextOfKinPhoneNo;
    }

    public String getNextOfKinAddress() {
        return nextOfKinAddress;
    }

    public void setNextOfKinAddress(String nextOfKinAddress) {
        this.nextOfKinAddress = nextOfKinAddress;
    }

    public String getPurposeOfLand() {
        return purposeOfLand;
    }

    public void setPurposeOfLand(String purposeOfLand) {
        this.purposeOfLand = purposeOfLand;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public void reset() {
        ourInstance = new EstateTransaction();
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public boolean isWillAcceptAlternatePlot() {
        return willAcceptAlternatePlot;
    }

    public void setWillAcceptAlternatePlot(boolean willAcceptAlternatePlot) {
        this.willAcceptAlternatePlot = willAcceptAlternatePlot;
    }
}
