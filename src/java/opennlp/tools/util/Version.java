///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2008 OpenNlp
// 
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
// 
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
// 
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.tools.util;

/**
 * The {@link Version} class represents the OpenNlp Tools library version.
 * 
 * The version has three parts:
 * 
 * Major: OpenNlp Tools libraries with a different major version are not interchangeable.
 * 
 * Minor: OpenNlp Tools libraries with an identical major version, but different
 *     minor version may be interchangeable. See release notes for further details.
 * 
 * Revision: OpenNlp Tools libraries with same major and minor version, but a different
 *     revision, are fully interchangeable.
 */
public class Version {
  
  private final int major;
  
  private final int minor;
  
  private final int revision;
  
  /**
   * Initializes the current instance with the provided
   * versions.
   * 
   * @param major
   * @param minor
   * @param revision
   */
  public Version(int major, int minor, int revision) {
    this.major = major;
    this.minor = minor;
    this.revision = revision;
  }
  
  /**
   * Retrieves the major version.
   * 
   * @return major version
   */
  public int getMajor() {
    return major;
  }
  
  /**
   * Retrieves the minor version.
   * 
   * @return minor version
   */
  public int getMinor() {
    return minor;
  }
  
  /**
   * Retrieves the revision version.
   * 
   * @return revision version
   */
  public int getRevision() {
    return revision;
  }
  
  /**
   * Retrieves the version string. 
   * 
   * The {@link #parse(String)} method can create an instance
   * of {@link Version} with the returned version value string.
   * 
   * @return the version value string
   */
  public String toString() {
    return Integer.toString(getMajor()) + "." + Integer.toString(getMinor()) + 
      "." + Integer.toString(getRevision());
  }
  
  
  /**
   * Return a new {@link Version} initialized to the value
   * represented by the specified {@link String}
   * 
   * @param version the string to be parsed
   * 
   * @return the version represented by the string value
   * 
   * @throws NumberFormatException if the string does
   * not contain a valid version
   */
  public static Version parse(String version){
    return null;
  }
  
  /**
   * Retrieves the current version of the OpenNlp Tools library.
   * 
   * @return the current version
   */
  public static Version currentVersion() {
    return new Version(1, 5, 0);
  }
}