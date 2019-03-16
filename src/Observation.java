public class Observation {
    private double[] attributes;
    private String deciseAttribute;
    private double destTo = 0;

    public void setAttributes(double[] attributes) {
        this.attributes = attributes;
    }

    public void setDeciseAttribute(String deciseAttribute) {
        this.deciseAttribute = deciseAttribute;
    }

    public double[] getAttributes() {
        return attributes;
    }

    public String getDeciseAttribute() {
        return deciseAttribute;
    }

    public void setDestTo(Observation unknownDest) {
        double[] unknownDestAttributes = unknownDest.getAttributes();
        double result = 0.0;
        for (int i = 0; i < unknownDestAttributes.length; i++) {
            result += Math.pow((unknownDestAttributes[i] - attributes[i]), 2.0);
        }
        destTo = Math.pow(result, 0.5);
    }

    public double getDestTo() {
        return destTo;
    }
}
