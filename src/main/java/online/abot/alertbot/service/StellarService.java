package online.abot.alertbot.service;


import online.abot.alertbot.domian.QqBinding;

//To listen to the stellar horizon
public interface StellarService {
        boolean subscribe(QqBinding qqBinding);
}
