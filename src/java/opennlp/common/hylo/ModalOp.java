///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2001 Jason Baldridge
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.common.hylo;

import opennlp.common.synsem.*;
import opennlp.common.unify.*;
import org.jdom.*;
import java.util.*;

public abstract class ModalOp extends HyloFormula {
    protected Mode _mode;
    protected LF _arg;

    protected ModalOp (Element e) {
	String atomLabel = e.getAttributeValue("m");
	if (atomLabel != null) {
	    _mode = new ModeLabel(atomLabel);
	    _arg = HyloHelper.getLF((Element)e.getChildren().get(0));
	} else {
	    List children = e.getChildren();
	    _mode = (Mode)HyloHelper.getLF((Element)children.get(0));
	    _arg = HyloHelper.getLF((Element)children.get(1));
	}
    }
    
    protected ModalOp (Mode mode, LF arg) {
	_mode = mode;
	_arg = arg;
    }

    public void deepMap (ModFcn mf) {
	_arg.deepMap(mf);
	mf.modify(this);
    }

    public boolean occurs (Variable var) {
	return _mode.occurs(var) || _arg.occurs(var);
    }

    protected boolean equals (ModalOp mo) {
	if (_mode.equals(mo._mode) && _arg.equals(mo._arg)) {
	    return true;
	} else {
	    return false;
	}
    }

    protected void unifyCheck (ModalOp mo) throws UnifyFailure {
	_mode.unifyCheck(mo._mode);
	_arg.unifyCheck(mo._arg);
    }

    
}
