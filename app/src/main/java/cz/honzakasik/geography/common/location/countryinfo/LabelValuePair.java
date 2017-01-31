package cz.honzakasik.geography.common.location.countryinfo;

public class LabelValuePair<T> {

    CharSequence label;
    T value;
    ValueType type;

    public LabelValuePair(CharSequence label, T value) {
        this.label = label;
        this.value = value;
        this.type = ValueType.TEXT_ONLY;
    }

    public LabelValuePair(CharSequence label, T value, ValueType type) {
        this.label = label;
        this.value = value;
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public CharSequence getLabel() {
        return label;
    }

    public ValueType getType() {
        return type;
    }
}
