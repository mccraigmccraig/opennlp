package opennlp.uima;

import com.ibm.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import com.ibm.uima.analysis_engine.annotator.AnnotatorContext;
import com.ibm.uima.analysis_engine.annotator.AnnotatorContextException;

public class UIMAUtil 
{
  public static final String TOKEN_TYPE_PARAMETER = "opennlp.uima.TokenType";

  public static String MODEL_PARAMETER = "opennlp.uima.ModelName";
  
  public static String SENTENCE_TYPE_PARAMETER = "opennlp.uima.SentenceType";

  public static String getRequiredParameter(AnnotatorContext context, 
        String parameter) throws AnnotatorConfigurationException
  {
    String value;
    
    try
    {
        value = (String) context.getConfigParameterValue(
                parameter);
    }
    catch (AnnotatorContextException e)
    {
        throw new AnnotatorConfigurationException(
                AnnotatorConfigurationException.STANDARD_MESSAGE_CATALOG,
                new Object[]{
                "tough shit, there is an internal error in the UIMA SDK," +
                "sorry"});
    }
    
    if (value == null)
    {
        throw new AnnotatorConfigurationException(
                AnnotatorConfigurationException.ONE_PARAM_REQUIRED,
                new Object[]{"The " + parameter + " is a " +
                "requiered parameter!"});
    }
    
    return value;
  }
}
