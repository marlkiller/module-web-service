/**
 * SecurityServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */
package com.voidm.module.webservice.webservice;


/**
 *  SecurityServiceCallbackHandler Callback class, Users can extend this class and implement
 *  their own receiveResult and receiveError methods.
 */
public abstract class SecurityServiceCallbackHandler {
    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     * @param clientData Object mechanism by which the user can pass in user data
     * that will be avilable at the time this callback is called.
     */
    public SecurityServiceCallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public SecurityServiceCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */
    public Object getClientData() {
        return clientData;
    }

    /**
     * auto generated Axis2 call back method for appLogin method
     * override this method for handling normal response from appLogin operation
     */
    public void receiveResultappLogin(
        SecurityServiceStub.AppLoginResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from appLogin operation
     */
    public void receiveErrorappLogin(Exception e) {
    }

    /**
     * auto generated Axis2 call back method for appLoginOut method
     * override this method for handling normal response from appLoginOut operation
     */
    public void receiveResultappLoginOut(
        SecurityServiceStub.AppLoginOutResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from appLoginOut operation
     */
    public void receiveErrorappLoginOut(Exception e) {
    }

    /**
     * auto generated Axis2 call back method for appModifyPw method
     * override this method for handling normal response from appModifyPw operation
     */
    public void receiveResultappModifyPw(
        SecurityServiceStub.AppModifyPwResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from appModifyPw operation
     */
    public void receiveErrorappModifyPw(Exception e) {
    }
}
