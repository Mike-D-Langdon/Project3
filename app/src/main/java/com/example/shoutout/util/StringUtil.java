package com.example.shoutout.util;

/**
 * A collection of string-related utility methods.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class StringUtil {

    /**
     * Extract the file extension out of a file name. Examples:
     * <table>
     *     <tr>
     *         <th>Input</th>
     *         <th>Output</th>
     *     </tr>
     *     <tr>
     *         <td>"myimage.png"</td>
     *         <td>".png"</td>
     *     </tr>
     *     <tr>
     *         <td>"myscript"</td>
     *         <td>""</td>
     *     </tr>
     *     <tr>
     *         <td>"weird_file.final.jpg"</td>
     *         <td>".jpg"</td>
     *     </tr>
     * </table>
     * @param fileName The file name to extract an extension out of
     * @return The file name extension, including the period (<code>.</code>) character. Returns an
     * empty string if no period (<code>.</code>) character was found.
     */
    public static String getFileNameExtension(String fileName) {
        final int indexOf = fileName.lastIndexOf('.');
        if (indexOf >= 0) {
            return fileName.substring(indexOf);
        }
        return "";
    }

    /**
     * Extract the file name without its extension out of a file name. Examples:
     * <table>
     *     <tr>
     *         <th>Input</th>
     *         <th>Output</th>
     *     </tr>
     *     <tr>
     *         <td>"myimage.png"</td>
     *         <td>"myimage"</td>
     *     </tr>
     *     <tr>
     *         <td>"myscript"</td>
     *         <td>"myscript"</td>
     *     </tr>
     *     <tr>
     *         <td>"weird_file.final.jpg"</td>
     *         <td>"weird_file.final"</td>
     *     </tr>
     * </table>
     * @param fileName The file name
     * @return The file name without its extension. Will just return the original file name if
     * no period (<code>.</code>) character was found.
     */
    public static String getFileNameWithoutExtension(String fileName) {
        final int indexOf = fileName.lastIndexOf('.');
        if (indexOf >= 0) {
            return fileName.substring(0, indexOf);
        }
        return fileName;
    }

}
