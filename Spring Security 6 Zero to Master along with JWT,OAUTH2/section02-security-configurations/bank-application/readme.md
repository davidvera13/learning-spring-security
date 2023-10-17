## Backend rest services
### Services without security
  - `/contact` - this service should accept the details from the contact us page in the UI and save to the DB
  - `/notices` - this service should send the notice details from the DB to the notices page in the UI
  
### Service with security
  - `/myAccount` - this service should send the account details of the logged in use from the DB to the UI
  - `/myBalance` - this service should send the balance of transaction details of the logged in user from the DB to the UI
  - `/myLoans` - this service should send the loan details of the logged in user from the DB to the UI
  - `/myCards` - this service should send the cards details of the logged in user from the DB to the UI