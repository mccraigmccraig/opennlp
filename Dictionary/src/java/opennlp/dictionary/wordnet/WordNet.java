package opennlp.dictionary.wordnet;

import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *  Command line interface to WordNet.<p>
 *  
 *  <table>
 *    <tr>
 *      <td width=8%>usage</td>
 *      <td width=2%>&nbsp;</td>
 *      <td width=60% valign="top" align="left">wn word [-hgla] [-n#] [-Ofile] -searchType [-searchType...]</td>
 *      <td>Perform WordNet search</td>
 *    </tr>
 *    <tr>
 *      <td width=8%>&nbsp;</td>
 *      <td width=2%>&nbsp;</td>
 *      <td width=60% valign="top" align="left">wn [-l]</td>
 *      <td>Display license information</td>
 *    </tr>
 *    <tr>
 *      <td width=10%>Where "wn" is:</td>
 *      <td width=0%>&nbsp;</td>
 *      <td width=60% valign="top" align="left">java -DWNSEARCHDIR=C:\nlp\WordNet\dict\ -jar output\dictionary-0.1.0.jar</td>
 *      <td>&nbsp;</td>
 *    </tr>
 *  </table>
 * <p>
 * Command line options:
 *  <table>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-h</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Display help text before search output</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-g</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Display gloss</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-l</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Display license and copyright notice</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-a</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Display lexicographer file information</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-o</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Display synset offset</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-O&lt;fname&gt;</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Output to file fname</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-s</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Display sense numbers in synsets</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-n#</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Search only sense number #</td>
 *    </tr>
 *  </table>
 *  <p>
 *  searchType is at least one of the following:<br>
 *
 *  <table>
 *
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-ants{n|v|a|r}</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Antonyms</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-hype{n|v}</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Hypernyms</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-hypo{n|v), -tree{n|v}</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Hyponyms & Hyponym Tree</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-entav</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Verb Entailment</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-syns{n|v|a|r}</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Synonyms (ordered by frequency)</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-smemn</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Member of Holonyms</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-ssubn</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Substance of Holonyms</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-sprtn</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Part of Holonyms</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-membn</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Has Member Meronyms</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-subsn</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Has Substance Meronyms</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-partn</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Has Part Meronyms</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-meron</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">All Meronyms</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-holon</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">All Holonyms</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-causv</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Cause to</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-pert{a|r}</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Pertainyms</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-attr{n|a}</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Attributes</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-nomn{n|v}</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Nominalizations</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-faml{n|v|a|r}</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Familiarity & Polysemy Count</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-framv</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Verb Frames</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-coor{n|v}</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Coordinate Terms (sisters)</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-simsv</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Synonyms (grouped by similarity of meaning)</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-hmern</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Hierarchical Meronyms</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-hholn</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Hierarchical Holonyms</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-grep{n|v|a|r}</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">List of Compound Words</td>
 *    </tr>
 *    <tr>
 *      <td width=5%>&nbsp;</td>
 *      <td width=15% valign="top" align="left">-over</td>
 *      <td width=2%>&nbsp;</td>
 *      <td valign="top" align="left">Overview of Senses</td>
 *    </tr>
 *
 *  </table>
 *  <p>
 *
 *  This class was created by heavily modifying the WordNet 1.7 code src/wn/wn.c
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    20 March 2002
 * @version    "$Id: WordNet.java,v 1.2 2002/03/26 19:16:36 mratkinson Exp $";
 */
public class WordNet {
    private Search searcher;
    private WNUtil wnUtil;
    private Morph morpher;
    private WNrtl wnRtl;

    static String Id = "$Id: WordNet.java,v 1.2 2002/03/26 19:16:36 mratkinson Exp $";

    private static String license =
            "This software and database is being provided to you, the LICENSEE, by  " +
            WNGlobal.lineSeparator +
            "Princeton University under the following license.  By obtaining, using  " +
            WNGlobal.lineSeparator +
            "and/or copying this software and database, you agree that you have " +
            WNGlobal.lineSeparator +
            "read, understood, and will comply with these terms and conditions.: " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Permission to use, copy, modify and distribute this software and  " +
            WNGlobal.lineSeparator +
            "database and its documentation for any purpose and without fee or  " +
            WNGlobal.lineSeparator +
            "royalty is hereby granted, provided that you agree to comply with  " +
            WNGlobal.lineSeparator +
            "the following copyright notice and statements, including the disclaimer,  " +
            WNGlobal.lineSeparator +
            "and that the same appear on ALL copies of the software, database and  " +
            WNGlobal.lineSeparator +
            "documentation, including modifications that you make for internal  " +
            WNGlobal.lineSeparator +
            "use or for distribution.  " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "WordNet 1.7 Copyright 2001 by Princeton University.  All rights reserved.  " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "THIS SOFTWARE AND DATABASE IS PROVIDED \"AS IS\" AND PRINCETON  " +
            WNGlobal.lineSeparator +
            "UNIVERSITY MAKES NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR  " +
            WNGlobal.lineSeparator +
            "IMPLIED.  BY WAY OF EXAMPLE, BUT NOT LIMITATION, PRINCETON  " +
            WNGlobal.lineSeparator +
            "UNIVERSITY MAKES NO REPRESENTATIONS OR WARRANTIES OF MERCHANT-  " +
            WNGlobal.lineSeparator +
            "ABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT THE USE  " +
            WNGlobal.lineSeparator +
            "OF THE LICENSED SOFTWARE, DATABASE OR DOCUMENTATION WILL NOT  " +
            WNGlobal.lineSeparator +
            "INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS OR  " +
            WNGlobal.lineSeparator +
            "OTHER RIGHTS." +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "The name of Princeton University or Princeton may not be used in  " +
            WNGlobal.lineSeparator +
            "advertising or publicity pertaining to distribution of the software  " +
            WNGlobal.lineSeparator +
            "and/or database.  Title to copyright in this software, database and  " +
            WNGlobal.lineSeparator +
            "any associated documentation shall at all times remain with  " +
            WNGlobal.lineSeparator +
            "Princeton University and LICENSEE agrees to preserve same.  "
             + WNGlobal.lineSeparator
            ;

    private static String dblicense =
            "  1 This software and database is being provided to you, the LICENSEE, by  " +
            WNGlobal.lineSeparator +
            "  2 Princeton University under the following license.  By obtaining, using  " +
            WNGlobal.lineSeparator +
            "  3 and/or copying this software and database, you agree that you have  " +
            WNGlobal.lineSeparator +
            "  4 read, understood, and will comply with these terms and conditions.: " +
            WNGlobal.lineSeparator +
            "  5  " +
            WNGlobal.lineSeparator +
            "  6 Permission to use, copy, modify and distribute this software and  " +
            WNGlobal.lineSeparator +
            "  7 database and its documentation for any purpose and without fee or  " +
            WNGlobal.lineSeparator +
            "  8 royalty is hereby granted, provided that you agree to comply with  " +
            WNGlobal.lineSeparator +
            "  9 the following copyright notice and statements, including the disclaimer,  " +
            WNGlobal.lineSeparator +
            "  10 and that the same appear on ALL copies of the software, database and  " +
            WNGlobal.lineSeparator +
            "  11 documentation, including modifications that you make for internal  " +
            WNGlobal.lineSeparator +
            "  12 use or for distribution.  " +
            WNGlobal.lineSeparator +
            "  13   " +
            WNGlobal.lineSeparator +
            "  14 WordNet 1.7 Copyright 2001 by Princeton University.  All rights reserved.  " +
            WNGlobal.lineSeparator +
            "  15  " +
            WNGlobal.lineSeparator +
            "  16 THIS SOFTWARE AND DATABASE IS PROVIDED \"AS IS\" AND PRINCETON  " +
            WNGlobal.lineSeparator +
            "  17 UNIVERSITY MAKES NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR  " +
            WNGlobal.lineSeparator +
            "  18 IMPLIED.  BY WAY OF EXAMPLE, BUT NOT LIMITATION, PRINCETON  " +
            WNGlobal.lineSeparator +
            "  19 UNIVERSITY MAKES NO REPRESENTATIONS OR WARRANTIES OF MERCHANT-  " +
            WNGlobal.lineSeparator +
            "  20 ABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT THE USE  " +
            WNGlobal.lineSeparator +
            "  21 OF THE LICENSED SOFTWARE, DATABASE OR DOCUMENTATION WILL NOT  " +
            WNGlobal.lineSeparator +
            "  22 INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS OR  " +
            WNGlobal.lineSeparator +
            "  23 OTHER RIGHTS.  " +
            WNGlobal.lineSeparator +
            "  24   " +
            WNGlobal.lineSeparator +
            "  25 The name of Princeton University or Princeton may not be used in  " +
            WNGlobal.lineSeparator +
            "  26 advertising or publicity pertaining to distribution of the software  " +
            WNGlobal.lineSeparator +
            "  28 and/or database.  Title to copyright in this software, database and  " +
            WNGlobal.lineSeparator +
            "  29 any associated documentation shall at all times remain with  " +
            WNGlobal.lineSeparator +
            "  30 Princeton University and LICENSEE agrees to preserve same.  "
             + WNGlobal.lineSeparator
            ;
    private static Options[] optlist = {
            new Options("-synsa", WNConsts.SIMPTR, WNConsts.ADJ, 0, "Similarity"),
            new Options("-antsa", WNConsts.ANTPTR, WNConsts.ADJ, 1, "Antonyms"),
            new Options("-perta", WNConsts.PERTPTR, WNConsts.ADJ, 0, "Pertainyms"),
            new Options("-attra", WNConsts.ATTRIBUTE, WNConsts.ADJ, 2, "Attributes"),
            new Options("-famla", WNConsts.FREQ, WNConsts.ADJ, 3, "Familiarity"),
            new Options("-grepa", WNConsts.WNGREP, WNConsts.ADJ, 4, "Grep"),
            new Options("-synsn", WNConsts.HYPERPTR, WNConsts.NOUN, 0, "Synonyms/Hypernyms (Ordered by Frequency)"),
            new Options("-simsn", WNConsts.RELATIVES, WNConsts.NOUN, 1, "Synonyms (Grouped by Similarity of Meaning)"),
            new Options("-antsn", WNConsts.ANTPTR, WNConsts.NOUN, 2, "Antonyms"),
            new Options("-coorn", WNConsts.COORDS, WNConsts.NOUN, 3, "Coordinate Terms (sisters)"),
            new Options("-hypen", -WNConsts.HYPERPTR, WNConsts.NOUN, 4, "Synonyms/Hypernyms (Ordered by Frequency)"),
            new Options("-hypon", WNConsts.HYPOPTR, WNConsts.NOUN, 5, "Hyponyms"),
            new Options("-treen", -WNConsts.HYPOPTR, WNConsts.NOUN, 6, "Hyponyms"),
            new Options("-holon", WNConsts.HOLONYM, WNConsts.NOUN, 7, "Holonyms"),
            new Options("-sprtn", WNConsts.ISPARTPTR, WNConsts.NOUN, 7, "Part Holonyms"),
            new Options("-smemn", WNConsts.ISMEMBERPTR, WNConsts.NOUN, 7, "Member Holonyms"),
            new Options("-ssubn", WNConsts.ISSTUFFPTR, WNConsts.NOUN, 7, "Substance Holonyms"),
            new Options("-hholn", -WNConsts.HHOLONYM, WNConsts.NOUN, 8, "Holonyms"),
            new Options("-meron", WNConsts.MERONYM, WNConsts.NOUN, 9, "Meronyms"),
            new Options("-subsn", WNConsts.HASSTUFFPTR, WNConsts.NOUN, 9, "Substance Meronyms"),
            new Options("-partn", WNConsts.HASPARTPTR, WNConsts.NOUN, 9, "Part Meronyms"),
            new Options("-membn", WNConsts.HASMEMBERPTR, WNConsts.NOUN, 9, "Member Meronyms"),
            new Options("-hmern", -WNConsts.HMERONYM, WNConsts.NOUN, 10, "Meronyms"),
            new Options("-nomnn", WNConsts.NOMINALIZATIONS, WNConsts.NOUN, 11, "Nominalizations"),
            new Options("-attrn", WNConsts.ATTRIBUTE, WNConsts.NOUN, 12, "Attributes"),
            new Options("-famln", WNConsts.FREQ, WNConsts.NOUN, 13, "Familiarity"),
            new Options("-grepn", WNConsts.WNGREP, WNConsts.NOUN, 14, "Grep"),
            new Options("-synsv", WNConsts.HYPERPTR, WNConsts.VERB, 0, "Synonyms/Hypernyms (Ordered by Frequency)"),
            new Options("-simsv", WNConsts.RELATIVES, WNConsts.VERB, 1, "Synonyms (Grouped by Similarity of Meaning)"),
            new Options("-antsv", WNConsts.ANTPTR, WNConsts.VERB, 2, "Antonyms"),
            new Options("-coorv", WNConsts.COORDS, WNConsts.VERB, 3, "Coordinate Terms (sisters)"),
            new Options("-hypev", -WNConsts.HYPERPTR, WNConsts.VERB, 4, "Synonyms/Hypernyms (Ordered by Frequency)"),
            new Options("-hypov", WNConsts.HYPOPTR, WNConsts.VERB, 5, "Troponyms (hyponyms)"),
            new Options("-treev", -WNConsts.HYPOPTR, WNConsts.VERB, 5, "Troponyms (hyponyms)"),
            new Options("-tropv", -WNConsts.HYPOPTR, WNConsts.VERB, 5, "Troponyms (hyponyms)"),
            new Options("-entav", WNConsts.ENTAILPTR, WNConsts.VERB, 6, "Entailment"),
            new Options("-causv", WNConsts.CAUSETO, WNConsts.VERB, 7, "\'Cause To\'"),
            new Options("-nomnv", WNConsts.NOMINALIZATIONS, WNConsts.VERB, 8, "Nominalizations"),
            new Options("-framv", WNConsts.FRAMES, WNConsts.VERB, 9, "Sample Sentences"),
            new Options("-famlv", WNConsts.FREQ, WNConsts.VERB, 10, "Familiarity"),
            new Options("-grepv", WNConsts.WNGREP, WNConsts.VERB, 11, "Grep"),
            new Options("-synsr", WNConsts.SYNS, WNConsts.ADV, 0, "Synonyms"),
            new Options("-antsr", WNConsts.ANTPTR, WNConsts.ADV, 1, "Antonyms"),
            new Options("-pertr", WNConsts.PERTPTR, WNConsts.ADV, 0, "Pertainyms"),
            new Options("-famlr", WNConsts.FREQ, WNConsts.ADV, 2, "Familiarity"),
            new Options("-grepr", WNConsts.WNGREP, WNConsts.ADV, 3, "Grep"),
            new Options("-over", WNConsts.OVERVIEW, WNConsts.ALL_POS, -1, "Overview"),
            };
    private static SearchStruct[] searchstr = {// index by search type type.
            new SearchStruct(),
            new SearchStruct(1, "-ants", "-ants{n|v|a|r}", "\t\tAntonyms"),
            new SearchStruct(1, "-hype", "-hype{n|v}", "\t\tHypernyms"),
            new SearchStruct(2, "-hypo, -tree", "-hypo{n|v), -tree{n|v}", "\tHyponyms & Hyponym Tree"),
            new SearchStruct(1, "-enta", "-entav\t", "\t\tVerb Entailment"),
            new SearchStruct(1, "-syns", "-syns{n|v|a|r}", "\t\tSynonyms (ordered by frequency)"),
            new SearchStruct(1, "-smem", "-smemn\t", "\t\tMember of Holonyms"),
            new SearchStruct(1, "-ssub", "-ssubn\t", "\t\tSubstance of Holonyms"),
            new SearchStruct(1, "-sprt", "-sprtn\t", "\t\tPart of Holonyms"),
            new SearchStruct(1, "-memb", "-membn\t", "\t\tHas Member Meronyms"),
            new SearchStruct(1, "-subs", "-subsn\t", "\t\tHas Substance Meronyms"),
            new SearchStruct(1, "-part", "-partn\t", "\t\tHas Part Meronyms"),
            new SearchStruct(1, "-mero", "-meron\t", "\t\tAll Meronyms"),
            new SearchStruct(1, "-holo", "-holon\t", "\t\tAll Holonyms"),
            new SearchStruct(1, "-caus", "-causv\t", "\t\tCause to"),
            new SearchStruct(), // PPLPTR - no specific search.
            new SearchStruct(), // SEEALSOPTR - no specific search.
            new SearchStruct(1, "-pert", "-pert{a|r}", "\t\tPertainyms"),
            new SearchStruct(1, "-attr", "-attr{n|a}", "\t\tAttributes"),
            new SearchStruct(), // verb groups - no specific pointer.
            new SearchStruct(1, "-nomn", "-nomn{n|v}", "\t\tNominalizations"),
            new SearchStruct(0, null, null, null), // syns - taken care of w/SIMPTR.
            new SearchStruct(1, "-faml", "-faml{n|v|a|r}", "\t\tFamiliarity & Polysemy Count"),
            new SearchStruct(1, "-fram", "-framv\t", "\t\tVerb Frames"),
            new SearchStruct(1, "-coor", "-coor{n|v}", "\t\tCoordinate Terms (sisters)"),
            new SearchStruct(1, "-sims", "-simsv\t", "\t\tSynonyms (grouped by similarity of meaning)"),
            new SearchStruct(1, "-hmer", "-hmern\t", "\t\tHierarchical Meronyms"),
            new SearchStruct(1, "-hhol", "-hholn\t", "\t\tHierarchical Holonyms"),
            new SearchStruct(), // wnescort - not used.
            new SearchStruct(1, "-grep", "-grep{n|v|a|r}", "\t\tList of Compound Words"),
            new SearchStruct(0, "-over", "-over\t", "\t\tOverview of Senses")
            };

    private static java.io.PrintStream out = System.out;
    private static java.io.PrintStream err = System.err;


    /**
     *  Constructor for the WordNet object
     *
     * @param  argv  The command line parameters
     * @since        0.1.0
     */
    public WordNet(String[] argv) {
        if (argv.length < 1) {
            printUsage();
            System.exit(-1);
        } else if (argv.length == 1 && "-l".equals(argv[0])) {
            printLicense();
            System.exit(-1);
        }
        wnRtl = new WNrtl();
        BinSearch binSearcher = new BinSearch();
        searcher = new Search(binSearcher);
        wnUtil = new WNUtil(binSearcher, searcher, wnRtl);
        searcher.setWNUtil(wnUtil, wnRtl);
        morpher = new Morph(binSearcher, searcher, wnRtl);
        wnUtil.setMorph(morpher);

        try {
            wnUtil.wnInit();
        } catch (Exception e) {// open database.
            displayMessage("wn: Fatal error - cannot open WordNet database");
            e.printStackTrace();
            System.exit(-1);
        }

    }


    /**
     *  Perform the WordNet search as directed by the command line parameters.
     *
     * @param  argv  The command line parameters
     * @return       If negative number of errors encountered, if positive the number
     *      of senses for the word.
     * @since        0.1.0
     */
    public int searchwn(String[] argv) {
        int whichsense = WNConsts.ALLSENSES;
        int help = 0;
        int errcount = 0;
        int outsenses = 0;

        if (argv.length == 1) {// print available searches for word.
            System.exit(do_isDefined(argv[0]));
        }

        // Parse command line options once and set flags.

        wnRtl.dFlag = false;
        wnRtl.fileInfoFlag = false;
        wnRtl.offsetFlag = false;
        wnRtl.wnsnsFlag = false;

        for (int i = 0; i < argv.length; i++) {
            if ("-g".equals(argv[i])) {
                wnRtl.dFlag = true;
            } else if ("-h".equals(argv[i])) {
                help++;
            } else if ("-l".equals(argv[i])) {
                printLicense();
            } else if (argv[i].startsWith("-n") && !argv[i].startsWith("-nomn")) {
                whichsense = Integer.parseInt(argv[i].substring(2));
            } else if ("-a".equals(argv[i])) {
                wnRtl.fileInfoFlag = true;
            } else if ("-o".equals(argv[i])) {
                wnRtl.offsetFlag = true;
            } else if ("-s".equals(argv[i])) {
                wnRtl.wnsnsFlag = true;
            } else if (argv[i].startsWith("-O")) {
                try {
                    String fname = argv[i].substring(2);
                    out = new PrintStream(new FileOutputStream(new File(fname)));
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }

        // Replace spaces with underscores before looking in database.

        wnUtil.strToLower(argv[0].replace(' ', '_'));

        // Look at each option in turn.  If it's not a command line option
        //   (which was processed earlier), perform the search requested.

        for (int j = 1; j < argv.length; j++) {
            if (!cmdopt(argv[j])) {// not a command line option.
                int i = getoptidx(argv[j]);
                if (i != -1) {
                    Options optptr = optlist[i];

                    // print help text before search output.
                    if (help > 0 && optptr.helpmsgidx >= 0) {
                        out.println(WNHelp.helptext[optptr.pos][optptr.helpmsgidx]);
                    }

                    if (optptr.pos == WNConsts.ALL_POS) {
                        for (int pos = 1; pos <= WNConsts.NUMPARTS; pos++) {
                            outsenses += doSearch(argv[0], pos, optptr.search, whichsense, optptr.label);
                        }
                    } else {
                        outsenses += doSearch(argv[0], optptr.pos, optptr.search, whichsense, optptr.label);
                    }
                } else {
                    displayMessage("wn: invalid search option: " + argv[j]);
                    errcount++;
                }
            }
        }
        return (errcount > 0) ? -errcount : outsenses;
    }


    /**
     *  Search for the word (or collocation) with options derived from the command
     *  line parameter(s).
     *
     * @param  searchWord  Word (or collocation) to search for
     * @param  pos         part of speech
     * @param  search      Search type.
     * @param  whichsense  sense or WNConsts.ALLSENSES.
     * @param  label       Label (should match search type).
     * @return             The total number of senses for the searchWord.
     * @since              0.1.0
     */
    private int doSearch(String searchWord, int pos, int search, int whichsense, String label) {
        String outBuf = searcher.findTheInfo(searchWord, pos, search, whichsense);
        int totalSenses = wnRtl.wnResults.printCount;
        if (outBuf != null && outBuf.length() > 0) {
            out.println();
            out.println(label + " of " + WNGlobal.partNames[pos] + " " + searchWord);
            out.println(outBuf);
        }
        String morphWord = morpher.morphStr(searchWord, pos);
        if (morphWord != null) {
            do {
                outBuf = searcher.findTheInfo(morphWord, pos, search, whichsense);
                totalSenses += wnRtl.wnResults.printCount;
                if (outBuf.length() > 0) {
                    out.println();
                    out.println(label + " of " + WNGlobal.partNames[pos] + " " + morphWord);
                    out.println(outBuf);
                }
            } while ((morphWord = morpher.morphStr(null, pos)) != null);
        }

        return totalSenses;
    }


    /**
     *  Print available searches for word.
     *
     * @param  searchWord  Word (or collocation) to search for.
     * @return             if error then -1, else if found then 1, else 0.
     * @since              0.1.0
     */
    private int do_isDefined(String searchWord) {
        int found = 0;

        if (searchWord.charAt(0) == '-') {
            displayMessage("wn: invalid search word");
            return -1;
        }

        // Print all valid searches for word in all parts of speech.

        wnUtil.strToLower(searchWord.replace(' ', '_'));

        for (int i = 1; i <= WNConsts.NUMPARTS; i++) {
            int search = searcher.isDefined(searchWord, i);
            if (search != 0) {
                printSearches(searchWord, i, search);
                found = 1;
            } else {
                out.println("No information available for " + WNGlobal.partNames[i] + " " + searchWord);
            }
            String morphWord = morpher.morphStr(searchWord, i);
            if (morphWord != null) {
                do {
                    search = searcher.isDefined(morphWord, i);
                    if (search != 0) {
                        printSearches(morphWord, i, search);
                        found = 1;
                    } else {
                        out.println();
                        out.println("No information available for " + WNGlobal.partNames[i] + " " + morphWord);
                    }
                } while ((morphWord = morpher.morphStr(null, i)) != null);
            }
        }
        return found;
    }


    /**
     *  Get available data for searchWord for that POS
     *
     * @param  searchWord  Word (or collocation) to search for.
     * @param  dbase       which POS database to look in.
     * @return             bit set of available data
     * @since              0.1.0
     */
    public int isDefined(String searchWord, int dbase) {
        if (searchWord.charAt(0) == '-') {
            return 0;
        }

        wnUtil.strToLower(searchWord.replace(' ', '_'));

        int search = searcher.isDefined(searchWord, dbase);
        String morphWord = morpher.morphStr(searchWord, dbase);
        if (morphWord != null) {
            do {
                search |= searcher.isDefined(morphWord, dbase);
            } while ((morphWord = morpher.morphStr(null, dbase)) != null);
        }
        return search;
    }


    /**
     *  Print the available searches for the word.
     *
     * @param  word    which may be searched for.
     * @param  dbase   adjective, adverb, noun, or verb.
     * @param  search  Search type performed.
     * @since          0.1.0
     */
    private void printSearches(String word, int dbase, int search) {
        out.println();
        out.println("Information available for " + WNGlobal.partNames[dbase] + " " + word);
        for (int j = 1; j <= WNConsts.MAXSEARCH; j++) {
            if ((search & (1 << j)) != 0 && searchstr[j].option != null) {
                out.print("\t");
                if (searchstr[j].num == 1) {
                    out.print(searchstr[j].template + WNGlobal.partChars.charAt(dbase));
                } else if (searchstr[j].num == 2) {
                    out.print(searchstr[j].template + WNGlobal.partChars.charAt(dbase) + searchstr[j].template2 + WNGlobal.partChars.charAt(dbase));
                }
                out.println(searchstr[j].helpstr);
            }
        }
    }


    /**
     *  Print the release and licence information.
     *
     * @since    0.1.0
     */
    private void printLicense() {
        out.println(getLicense());
    }

    public String getLicense() {
        return "WordNet Release " + WNGlobal.wnRelease + WNGlobal.lineSeparator +
               WNGlobal.lineSeparator + license;
    }

    /**
     *  The main program for the WordNet class
     *
     * @param  argv  The command line arguments
     * @since        0.1.0
     */
    public static void main(String[] argv) {
        WordNet wordNet = new WordNet(argv);
        System.exit(wordNet.searchwn(argv));
    }


    /**
     *  print the command line parameter usage.
     *
     * @since    0.1.0
     */
    public static void printUsage() {
        System.out.println();
        System.out.println("usage: wn word [-hgla] [-n#] -searchType [-searchType...]");
        System.out.println("       wn [-l]");
        System.out.println();
        System.out.println("\t-h\t\tDisplay help text before search output");
        System.out.println("\t-g\t\tDisplay gloss");
        System.out.println("\t-l\t\tDisplay license and copyright notice");
        System.out.println("\t-a\t\tDisplay lexicographer file information");
        System.out.println("\t-o\t\tDisplay synset offset");
        System.out.println("\t-O<fname>\t\tOutput to file fname");
        System.out.println("\t-s\t\tDisplay sense numbers in synsets");
        System.out.println("\t-n#\t\tSearch only sense number #");
        System.out.println();
        System.out.println("searchType is at least one of the following:");

        for (int i = 1; i <= WNConsts.OVERVIEW; i++) {
            if (searchstr[i].option != null) {
                System.out.println("\t" + searchstr[i].option + searchstr[i].helpstr);
            }
        }
    }


    /**
     *  Write an error message to System.err.
     *
     * @param  msg  The Message to write.
     * @since       0.1.0
     */
    public static void errorMessage(String msg) {
        System.err.println(msg);
    }


    /**
     *  Display a message to the "err" stream.
     *
     * @param  msg  Message to display
     * @since       0.1.0
     */
    public static void displayMessage(String msg) {
        err.println(msg);
    }


    /**
     *  Get index into the parameter options list.
     *
     * @param  searchType  Parameter search type.
     * @return             Index into the parameter options list or -1 if not
     *                     found.
     * @since              0.1.0
     */
    private static int getoptidx(String searchType) {
        if (searchType == null || searchType.equals("")) {
            return -1;
        }
        for (int i = 0; i < optlist.length; i++) {
            if (searchType.equals(optlist[i].option)) {
                return i;
            }
        }
        return -1;
    }


    /**
     *  Is this command line parameter an option.
     *
     * @param  str  The command line parameter.
     * @return      <tt>true</tt> if this command line parameter is an option.
     * @since       0.1.0
     */
    private static boolean cmdopt(String str) {
        if (str == null || str.equals("")) {
            return false;
        }

        if (str.equals("-g") ||
                str.equals("-h") ||
                str.equals("-o") ||
                str.equals("-l") ||
                str.equals("-a") ||
                str.equals("-s") ||
                str.startsWith("-O") ||
                (str.startsWith("-n") || str.startsWith("-nomn"))) {
            return true;
        } else {
            return false;
        }
    }


    public void setOut(PrintStream o) {
        out = o;
    }
    
    public void setErr(PrintStream e) {
        err = e;
    }
    
    
    public Search getSearcher() {
        return searcher;
    }
    
    
    /**
     *  This holds the various command line options and associated data.
     *
     * @author     Mike Atkinson (mratkinson)
     * @since      0.1.0
     * @created    20 March 2002
     */
    private static class Options {
        /**
         *  user's search request.
         *
         * @since    0.1.0
         */
        public String option;
        /**
         *  search to pass findTheInfo().
         *
         * @since    0.1.0
         */
        public int search;
        /**
         *  part-of-speech to pass findTheInfo().
         *
         * @since    0.1.0
         */
        public int pos;
        /**
         *  index into help message table.
         *
         * @since    0.1.0
         */
        public int helpmsgidx;
        /**
         *  text for search header message.
         *
         * @since    0.1.0
         */
        public String label;


        /**
         *  Constructor for the Options object
         *
         * @param  option      user's search request.
         * @param  search      search to pass findTheInfo().
         * @param  pos         part-of-speech to pass findTheInfo().
         * @param  helpmsgidx  index into help message table.
         * @param  label       text for search header message.
         * @since              0.1.0
         */
        public Options(String option, int search, int pos, int helpmsgidx, String label) {
            this.option = option;
            this.search = search;
            this.pos = pos;
            this.helpmsgidx = helpmsgidx;
            this.label = label;
        }
    }


    /**
     *  This contains data for the search options.
     *
     * @author     Mike Atkinson (mratkinson)
     * @since      0.1.0
     * @created    22 March 2002
     */
    private static class SearchStruct {
        /**
         *  index by search type type
         *
         * @since    0.1.0
         */
        public int num;
        /**
         *  template for generic search message.
         *
         * @since    0.1.0
         */
        public String template;
        /**
         *  alternative template for generic search message.
         *
         * @since    0.1.0
         */
        public String template2;
        /**
         *  text for help message showing command lline options.
         *
         * @since    0.1.0
         */
        public String option;
        /**
         *  descriptive text for help message.
         *
         * @since    0.1.0
         */
        public String helpstr;


        /**
         *  Constructor for the SearchStruct object
         *
         * @since    0.1.0
         */
        public SearchStruct() {
            this.num = 0;
            this.template = "";
            this.template2 = "";
            this.option = "";
            this.helpstr = "";
        }


        /**
         *  Constructor for the SearchStruct object
         *
         * @param  num       index by search type type
         * @param  template  template for generic search message.
         * @param  option    text for help message showing command lline options.
         * @param  helpstr   descriptive text for help message.
         * @since            0.1.0
         */
        public SearchStruct(int num, String template, String option, String helpstr) {
            this.num = num;
            this.template = template;
            this.template2 = "";
            this.option = option;
            this.helpstr = helpstr;
        }


        /**
         *  Constructor for the SearchStruct object
         *
         * @param  num        index by search type type
         * @param  template   Description of Parameter
         * @param  template2  template for generic search message.
         * @param  option     text for help message showing command lline options.
         * @param  helpstr    descriptive text for help message.
         * @since             0.1.0
         */
        public SearchStruct(int num, String template, String template2, String option, String helpstr) {
            this.num = num;
            this.template = template;
            this.template2 = "";
            this.option = option;
            this.helpstr = helpstr;
        }
    }
}

