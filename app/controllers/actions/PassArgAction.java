package controllers.actions;


import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.HashMap;
import java.util.concurrent.CompletionStage;

public class PassArgAction extends Action.Simple {
    @Override
    public CompletionStage<Result> call(Http.Context ctx) {
        ctx.args.put("HashMap-object", new HashMap<String,String>(){{put("k1","value1");put("k2","value2");}}  );
        ctx.session().put("session-PassArgAction","Created");
        return delegate.call(ctx);
    }
}
