package com.faast.mobile.apps;

public class Users {

    String InvNum;
    String InvDate;
    String InvDueDate;
    String EmailId;
    String Address;
    String MobileNo;
    String InvPrice;
    Double InvTax;
    String FirstName;
    String LastName;
    String fpstatus;
    String month;
    String data;
    String paymentdate;
    String paymentamount;
    String paymentMethod;
    String amount;
    String usagestartdate,usagesessiontime,consumeddata,usagegrandtotal,usagestopdate;
    String ticketId,ticketSubject,ticketStatus,ticketCreation,ticketComment,supportTicketId;

    public void setSupportTicketId(String supportTicketId) {
        this.supportTicketId = supportTicketId;
    }

    public String getSupportTicketId() {
        return supportTicketId;
    }

    public void setTicketCreation(String ticketCreation) {
        this.ticketCreation = ticketCreation;
    }

    public String getTicketCreation() {
        return ticketCreation;
    }

    public void setTicketCommemt(String ticketComment) {
        this.ticketComment = ticketComment;
    }

    public String getTicketComment() {
        return ticketComment;
    }


    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketSubject(String ticketSubject) {
        this.ticketSubject = ticketSubject;
    }
    public String getTicketSubject() {
        return ticketSubject;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }


    public void setUsagestopdate(String usagestopdate) {
        this.usagestopdate = usagestopdate;
    }

    public String getUsagestopdate() {
        return usagestopdate;
    }

    public void setUsagestartdate(String usagestartdate) {
        this.usagestartdate = usagestartdate;
    }

    public String getUsagestartdate() {
        return usagestartdate;
    }

    public void setUsagesessiontime(String usagesessiontime) {
        this.usagesessiontime = usagesessiontime;
    }
    public String getUsagesessiontime() {
        return usagesessiontime;
    }

    public void setConsumeddata(String consumeddata) {
        this.consumeddata = consumeddata;
    }
    public String getConsumeddata() {
        return consumeddata;
    }

    public void setUsagegrandtotal(String usagegrandtotal) {
        this.usagegrandtotal = usagegrandtotal;
    }
    public String getUsagegrandtotal() {
        return usagegrandtotal;
    }

    public void setMonth(String month) {
        this.month= month;
    }
    public String getMonth() {
        return month;
    }

    public void setAmount(String month) {
        this.month= month;
    }
    public String getAmount() {
        return month;
    }

    public void setPaymentDate(String paymentdate){ this.paymentdate=paymentdate; }
    public String getPaymentDate() { return paymentdate; }

    public void setPaymentAmount(String paymentamount){ this.paymentamount=paymentamount; }
    public String getPaymentAmount() { return paymentamount; }

    public void setPaymentMethod(String paymentMethod){ this.paymentMethod=paymentMethod; }
    public String getPaymentMethod() { return paymentMethod; }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }


    public void setInvNum(String InvNum) {
        this.InvNum= InvNum;
    }
    public String getInvNum() {
        return InvNum;
    }

    public void setInvDate(String InvDate) {
        this.InvDate = InvDate;
    }

    public String getInvDate() {
        return InvDate;
    }

    public void setInvPrice(String InvPrice) {
        this.InvPrice = InvPrice;
    }

    public String getInvPrice() {
        return InvPrice;
    }

    public void setInvTax(Double InvTax)
    {
        this.InvTax = InvTax;
    }

    public Double getInvTax() {
        return InvTax;
    }

    public String getInvDueDate() {
        return InvDueDate;
    }

    public void setInvDueDate(String  InvDueDate) {

        this.InvDueDate = InvDueDate;
    }

}