Changelog
=========

## 5.1    -   28/03/2017

*   Activities track: 
    -   All activities are getting stored in the database which are done by the users.
    -   Capturing the amount in Razor payment module.
*   Data Usage 
    -   Added swipe-able tab layout for data usage and showing message as “No detailed usage report of this month” if no data usage in current month.
*   Change Password
    -   Showing message as "allowed characters 0-9 A-Z a-z" and Text boxes allow only 0-9 a-z A-z characters.
    -   Added animations when opening new activity and closing it:
*   Top-ups
    -   Changed the design of the layout.
*   Internet Connection
    -   Earlier if internet is not connected then app was crashing so now app shows alert box with the message “Internet connection required”.
*   Status of Account
    -   If customer’s account is disabled, then shows alert box showing that “Your account has been disabled due to non-payment.”


## 5.2    -   03/04/2017
*   Notification
    -   Notification was not working means capturing notification message and showing into the dialogue box was not working.

## 5.4    -   24/04/2017
*   Payment capturing.
    -   Enabling the account after payment who has done payment after due date over.
*   Notification problem.
    -   Changed app_version page only added faast symbol.
    -   Storing all ucp activities done ny the app.

## 5.5    -   17/05/2017
*   Automated topups
    -   showing the city wise plans for. eg for belgaum people belgaum plans and for gokak users gokak plans
    -   updating ucp_activity table

## 5.6    -   24/05/2017
*   Home Page:
    -   Swapped logout and support menus.
 
*   Support Module:
    -   Redesigned the support module.
    -   This layout allows to create tickets with using following menus by showing only most important 2 steps .
*   Slow internet:
    -   Allows to create ticket when there will be slow internet occurs.
    -   No Power On Fiber Modem:
    -   Allows to create ticket when there will be no power on fiber modem.
*   Red LOS On Fiber Modem:
    -   Allows to create ticket when red light will be on fiber modem.
*   No Light On PoE Adaptor:
    -   Allows to create ticket when no light will be on PoE adaptor.
*   Others:
    -   Allows to create the tickets which different from mentioned above.
*   Tickets history:
    -   Shows list of all tickets raised earlier.

## 5.7    -   05/11/2017
*   Home Page
    -   Added alert box when there is outstanding amount
    -   Solved app crashing was happening in kitkat


## 5.9    -   08/08/2017

*   Plan Change:
    -   Changed error message as "A request for plan change is already pending, kindly wait for current bill cycle to complete." for when user is requesting for plan change in same month in which current plan is activated.
    -   Changed php files according to 10% of extra speed and postspeed for all plans and current plan as well.
*   Support Module:
    -	Added conversation in ticket history module and created php file for it. and all user commented thing we are making as public and inserting type as 1 in the database
    -   In ticket history list earlier we were not displaying create date , in this release it has been added.

*   Topups:
    -   Earlier we were getting vatpercent from different table now we are fetching it from rm_setting table.

*   Crashes:
    -   Moved all pictures and icons to mipmap folder from drawable folder.
*   Home page: from php file we were sending int and string data so in android it was not getting recognize which is int and string so because of that in nougat crash was happening that has been solved.



## 6.1    -   28/08/2017

*   FAAST Prime:
    -   Added new module called �FAAST Prime� in menu slider.
    -   which is to be requested by customer to upgrade the speed of the internet.
    -   This module allows the customer to make request to become prime member using �SIGN UP� button.
    -   Once requested �SIGN UP� button will be invisible for those customer who has requested already.
 
*   UCP Activity:
    -   Earlier we were not storing the SMS which were sent by admin to customers and partners.
    -   So we are dynamic image slider means we can display images in slider from database.
    
*   FAAST Prime:
    -   Changed FAAST Prime image.
    
    
## 6.3    -   27/10/2017

*   Deactivated Users:
    -   If account has been deactivated and there is an outstanding amount so app allows login but shows dialogue box saying “your account has been deactivated, kindly tap OK to make the payment towards the outstanding balance”
    -   If there is no outstanding amount, shows dialogue box saying “Login failure, kindly contact customer care for more information.”
    -   If user clicks on Ok it takes to page where pending invoices will be shown with pay button.
    -   After Successful payment, Invoices will be cleared and one more dialogue box shows saying “Do you want to continue the service?” with YES and NO
    -   If User taps Yes button user group will change to PPPoE Home that is group id ‘4’
    -   If user taps NO button group will be as it is as earlier but Cancellation Support Ticket will be created with Subject : Cancellation Support Ticket and Comment : Customer cleared pending amount and opted for deactivation of service, team, kindly recover the device. And sends SMS to partner, copartner and customer and sends mail to admin.
        And last Login page will be displayed.

*   Cancelled Users:
    -   If account has been cancelled and there is an outstanding amount so app allows login but shows dialogue box saying “your account has been deactivated, kindly tap OK to make the payment towards the outstanding balance”.
    -   If there is no outstanding amount, shows dialogue box saying “Login failure, kindly contact customer care for more information.”
    -   If user clicks on Ok it takes to page where pending invoices will be shown with pay button.
    -   After Successful payment, Invoices will be cleared and It will shift to login page.

*   Home Page:
    -   If user account is deactivated or Cancelled and if he/she is already logged into the app then It will automatically logged out from the app and it will jump to login page.
    -   If user tries to login then above steps will be continued with respective of their account status.

*   FAAST Daily Plans:
    -   Earlier for FAAST daily plans total data was not showing
    -   Now we have added one more textview to show total data of month.

## 6.6    -   14/11/2017

* FAAST Prime :
    -   Showing FAAST prime price 500 + Taxes for HOME Customers.
    -   Showing FAAST prime price 1000 + Taxes for SMB Customers.
    -   Sign Up button was visible until FAAST member activation now solved the issue as soon as user requests, Sign Up button will be invisible.
*   Plan Change:
    -   Showing monthly data for all plans
    -   Added monthly word after data limit
*   TopUps:
    -   Showing error message as “Sorry, TopUps are currently not allowed for DAILY plans.” For DAILY customers.
