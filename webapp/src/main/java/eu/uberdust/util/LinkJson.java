package eu.uberdust.util;

/**
 * POJO class for holding parameters for JSON representation of a link.
 */
public final class LinkJson {

    /**
     * Link source.
     */
    private String linkSource;

    /**
     * Link target.
     */
    private String linkTarget;

    /**
     * Constructor.
     */
    public LinkJson() {
        // empty constructor
    }

    /**
     * Constructor.
     * @param linkSource link source.
     * @param linkTarget link target.
     */
    public LinkJson(final String linkSource, final String linkTarget) {
        this.linkSource = linkSource;
        this.linkTarget = linkTarget;
    }

    /**
     * Returns link source.
     * @return link source.
     */
    public String getLinkSource() {
        return linkSource;
    }

    /**
     * Sets link source.
     * @param linkSource link source.
     */
    public void setLinkSource(final String linkSource) {
        this.linkSource = linkSource;
    }

    /**
     * Returns link target.
     * @return link target.
     */
    public String getLinkTarget() {
        return linkTarget;
    }

    /**
     * Sets link target.
     * @param linkTarget link target.
     */
    public void setLinkTarget(final String linkTarget) {
        this.linkTarget = linkTarget;
    }
}
