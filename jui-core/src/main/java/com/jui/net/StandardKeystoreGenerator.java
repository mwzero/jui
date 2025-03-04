package com.jui.net;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class StandardKeystoreGenerator {

    private final String keystorePath;
    private final String password;
    private final String alias = "selfsigned";

    public StandardKeystoreGenerator(String keystorePath, String password) {
    	
        this.keystorePath = keystorePath;
        this.password = password;
    }

    /**
     * Crea un keystore con un certificato auto-firmato.
     * @return 
     */
    public KeyStore createKeystore() throws Exception {
    	
        // Genera una coppia di chiavi
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        // Crea un certificato auto-firmato
        Certificate certificate = selfSign(keyPair);

        // Crea un keystore e salva la chiave privata e il certificato
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(null, null);
        keystore.setKeyEntry(alias, keyPair.getPrivate(), password.toCharArray(),
                new Certificate[]{certificate});

        try (FileOutputStream fos = new FileOutputStream(keystorePath)) {
            keystore.store(fos, password.toCharArray());
        }

        System.out.println("Keystore creato: " + keystorePath);
        
        return keystore;
    }

    /**
     * Genera un certificato auto-firmato utilizzando API standard.
     * @throws CertificateException 
     */
    public static Certificate selfSign(KeyPair keyPair) throws OperatorCreationException,  IOException, CertificateException
    {
        Provider bcProvider = new BouncyCastleProvider();
        Security.addProvider(bcProvider);

        long now = System.currentTimeMillis();
        Date startDate = new Date(now);

        BigInteger certSerialNumber = new BigInteger(Long.toString(now)); // <-- Using the current timestamp as the certificate serial number

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1); // <-- 1 Yr validity
        Date endDate = calendar.getTime();

        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate());

        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
        		new X500Name("CN=My Self-Signed Certificate, OU=My Organization, O=My Company, C=IT"), 
        		certSerialNumber, 
        		startDate, 
        		endDate, 
        		new X500Name("CN=My Self-Signed Certificate, OU=My Organization, O=My Company, C=IT"), 
        		keyPair.getPublic());

        // Basic Constraints
        BasicConstraints basicConstraints = new BasicConstraints(true); // <-- true for CA, false for EndEntity
        certBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, basicConstraints); // Basic Constraints is usually marked as critical.
        return new JcaX509CertificateConverter().setProvider(bcProvider).getCertificate(certBuilder.build(contentSigner));
    }
}
