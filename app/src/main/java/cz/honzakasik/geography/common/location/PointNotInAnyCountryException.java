package cz.honzakasik.geography.common.location;

public class PointNotInAnyCountryException extends Exception {

    public PointNotInAnyCountryException(String detailMessage) {
        super(detailMessage);
    }

    public PointNotInAnyCountryException() {
    }
}
