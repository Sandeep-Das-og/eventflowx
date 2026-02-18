*** Settings ***
Library    RequestsLibrary
Library    Collections

*** Variables ***
${BASE_URL}    http://localhost:8081

*** Test Cases ***
Create Booking Then Fetch Wallet
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${body}=    Create Dictionary    customerName=alice    eventName=concert-night
    ${create_resp}=    POST    ${BASE_URL}/bookings    json=${body}    headers=${headers}
    Should Be Equal As Integers    ${create_resp.status_code}    201

    ${wallet_resp}=    GET    ${BASE_URL}/wallets/alice
    Should Be Equal As Integers    ${wallet_resp.status_code}    200
    ${wallet_json}=    Set Variable    ${wallet_resp.json()}
    Dictionary Should Contain Key    ${wallet_json}    userId
    Should Be Equal    ${wallet_json}[userId]    alice

Credit Wallet
    ${credit_resp}=    POST    ${BASE_URL}/wallets/alice/credit?amount=100
    Should Be Equal As Integers    ${credit_resp.status_code}    200
    ${credit_json}=    Set Variable    ${credit_resp.json()}
    Dictionary Should Contain Key    ${credit_json}    balance
