package com.oneandone.iocunit.jta.narayana;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jboss.weld.transaction.spi.TransactionServices;

import com.arjuna.ats.jta.cdi.TransactionExtension;
import com.oneandone.cdi.weldstarter.WeldSetup;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.iocunit.jta.narayana.cdi.CDITransactionProducers;
import com.oneandone.iocunit.jta.narayana.cdi.CDITransactionServices;

public class NarayanaTestExtensionService implements TestExtensionService {
    @Override
    public List<Class<?>> testClasses() {
        List<Class<?>> result = new ArrayList<Class<?>>() {
            private static final long serialVersionUID = -1661631254833065243L;

            {
//                add(TransactionalInterceptorRequired.class);
//                add(TransactionalInterceptorRequiresNew.class);
//                add(TransactionalInterceptorMandatory.class);
//                add(TransactionalInterceptorNever.class);
//                add(TransactionalInterceptorNotSupported.class);
//                add(TransactionalInterceptorSupports.class);
                add(TransactionExtension.class);
            }
        };
        return result;
    }

    @Override
    public void preStartupAction(WeldSetupClass weldSetup, Class clazz, Method method) {

        if(weldSetup.isWeld3()) {
            if(!weldSetup.getBeanClasses().contains(CDITransactionProducers.class.getName())) {
                weldSetup.getBeanClasses().add(CDITransactionProducers.class.getName());
            }
        }
        weldSetup.addService(new WeldSetup.ServiceConfig(TransactionServices.class, new CDITransactionServices()));
    }
}
