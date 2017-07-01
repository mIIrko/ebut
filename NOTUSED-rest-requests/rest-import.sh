#!/bin/bash

# colour output
# https://stackoverflow.com/a/5947802
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[1;36m'
NC='\033[0m' # No Color

CURL_CMD="curl --request POST"
CURL_URL="--url localhost:8080/rest/service"
CURL_HEADER_ACCEPT="--header 'Accept: application/xml'"
CURL_HEADER_CONTENT_TYPE="--header 'Content-Type: application/xml'"
CURL_HEADER_CONTENT_LENGTH="--header 'Content-Length: "

echo
printf "${BLUE}EBUT REST IMPORT${NC}"
echo
echo
while true
do
    echo "0 - exit"
    echo "1 - no file in request"
    echo "2 - not well formed"
    echo "3 - not valid"
    echo "4 - not existing supplier"
    echo "5 - successful import"
    echo "6 - same article, other supplier"
    echo
    echo -n "Choose option: "
    read userinput

    if [[ $userinput == 0 ]]; then
        exit
    elif [[ $userinput == 1 ]]; then
        # no file in request
        size=0
        request="$CURL_CMD $CURL_HEADER_ACCEPT $CURL_HEADER_CONTENT_TYPE $CURL_HEADER_CONTENT_LENGTH$size' $CURL_URL"
    elif [[ $userinput == 2 ]]; then
        # not well formed
        data=`cat not-wellformed.xml`
        size=`stat --printf="%s" not-wellformed.xml`
        request="$CURL_CMD $CURL_HEADER_ACCEPT $CURL_HEADER_CONTENT_TYPE $CURL_HEADER_CONTENT_LENGTH$size' --data '$data' $CURL_URL"
    elif [[ $userinput == 3 ]]; then
        # not valid
        data=`cat not-valid.xml`
        size=`stat --printf="%s" not-valid.xml`
        request="$CURL_CMD $CURL_HEADER_ACCEPT $CURL_HEADER_CONTENT_TYPE $CURL_HEADER_CONTENT_LENGTH$size' --data '$data' $CURL_URL"
    elif [[ $userinput == 4 ]]; then
        # not existing supplier
        data=`cat not-existing-supplier.xml`
        size=`stat --printf="%s" not-existing-supplier.xml`
        request="$CURL_CMD $CURL_HEADER_ACCEPT $CURL_HEADER_CONTENT_TYPE $CURL_HEADER_CONTENT_LENGTH$size' --data '$data' $CURL_URL"
    elif [[ $userinput == 5 ]]; then
       # successful import
       data=`kn-media-store.xml`
       size=`stat --printf="%s" kn-media-store.xml`
        request="$CURL_CMD $CURL_HEADER_ACCEPT $CURL_HEADER_CONTENT_TYPE $CURL_HEADER_CONTENT_LENGTH$size' --data '$data' $CURL_URL"
    elif [[ $userinput == 6 ]]; then
        # same article, other supplier
        data=`cat same-articles-other-supplier.xml`
        size=`stat --printf="%s" same-articles-other-supplier.xml`
        request="$CURL_CMD $CURL_HEADER_ACCEPT $CURL_HEADER_CONTENT_TYPE $CURL_HEADER_CONTENT_LENGTH$size' --data '$data' $CURL_URL"
    else
        echo
        printf "${RED}Wrong input...${NC}"
        echo
        echo
    fi

    if [[ $request != "" ]]; then
        echo
        printf "${GREEN}Request${NC}  = "
        echo $request
        response=`$request` # calls the curl command
        echo
        printf "${GREEN}Response${NC} = "
         if [[ $response != "" ]]; then
            printf "${GREEN}Response${NC} = "
            echo $response | xmllint --format -
        else
            printf "${RED}No response...server down? Exit system now!${NC}"
            exit
        fi
        echo
        echo
    fi
done
