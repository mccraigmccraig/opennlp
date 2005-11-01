///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2003 Thomas Morton
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
package opennlp.tools.coref.resolver;

import java.io.IOException;

import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.DiscourseModel;
import opennlp.tools.coref.mention.MentionContext;

/** Interface for coreference resolvers. */
public interface Resolver {

  /** Returns true if this resolver is able to resolve the referening experession of the same type
   * as the specified mention.
   * @param mention The mention being considered for resolution. 
   * @return true if the resolver handles this type of refering
   * expression, false otherwise.
   */
  public boolean canResolve(MentionContext mention);

  /** Resolve this refering extression to a discourse entity in the discourse model.
   * @param ec the refering expression. 
   * @param dm the discourse model.
   * @return the discourse entity which the resolver beleives this
   * refering expression refers to or null if no discourse entity is
   * coreferent with the refering expression. */
  public DiscourseEntity resolve(MentionContext ec, DiscourseModel dm);

  /** Uses the specified mention and discourse model to train this resolver.
   * All mentions sent to this method need to have their id fields set to indicate coreference
   * relationships.    
   * @param mention The mention which is being used for training.
   * @param model the discourse model.
   * @return the discourse entity which is refered to by the refering
   * expression or null if no discourse entity is referenced.
   */ 
  public DiscourseEntity retain(MentionContext mention, DiscourseModel model);

  /** Retrains model on examples for which retain was called.  */
  public void train() throws IOException;
}
