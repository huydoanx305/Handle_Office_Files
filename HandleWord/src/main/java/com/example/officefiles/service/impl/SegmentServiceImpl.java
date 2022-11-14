package com.example.officefiles.service.impl;

import com.example.officefiles.entity.Document;
import com.example.officefiles.entity.Segment;
import com.example.officefiles.repository.DocumentRepository;
import com.example.officefiles.repository.SegmentRepository;
import com.example.officefiles.service.SegmentService;
import org.docx4j.TextUtils;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class SegmentServiceImpl implements SegmentService {
    private final SegmentRepository segmentRepository;

    private final DocumentRepository documentRepository;

    private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));

    public SegmentServiceImpl(SegmentRepository segmentRepository, DocumentRepository documentRepository) {
        this.segmentRepository = segmentRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    @Transactional
    public void writeDocx(Long documentId) throws Exception {
//        WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
//        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
//        ObjectFactory factory = Context.getWmlObjectFactory();
//        for(Segment paragraph : segmentRepository.findByDocument_IdAnAndPaAndParagraphNull(documentId)) {
//            P p = factory.createP();
//            for(Segment run : paragraph.getRuns()) {
//                R r = factory.createR();
//                Text t = factory.createText();
//                t.setValue(run.getText());
//                if (run.getText().startsWith(" ") || run.getText().endsWith(" ") ) {
//                    t.setSpace("preserve");
//                }
//                RPr rpr = factory.createRPr();
//                BooleanDefaultTrue style = new BooleanDefaultTrue();
//                if(run.getBold())
//                    rpr.setB(style);
//                if(run.getStrike())
//                    rpr.setStrike(style);
//                if(run.getItalic())
//                    rpr.setI(style);
//                if(run.getUnderlineEnumeration() != null) {
//                    U u = factory.createU();
//                    u.setVal(run.getUnderlineEnumeration());
//                    rpr.setU(u);
//                }
//                r.setRPr(rpr);
//                r.getContent().add(t);
//                p.getContent().add(r);
//            }
//            mainDocumentPart.getContent().add(p);
//        }
//        Optional<Document> document = documentRepository.findById(documentId);
//        Path path = CURRENT_FOLDER.resolve(CommonConstant.staticPath)
//                .resolve(CommonConstant.responsePath).resolve(document.get().getFileName());
//        if (!Files.exists(CURRENT_FOLDER.resolve(CommonConstant.staticPath).resolve(CommonConstant.responsePath))) {
//            Files.createDirectories(CURRENT_FOLDER.resolve(CommonConstant.staticPath).resolve(CommonConstant.responsePath));
//        }
//        wordPackage.save(new File(String.valueOf(path)));
    }

    @Override
    @Transactional
    public void saveSegment(List<Object> jaxbNodes, Document document) throws Exception {
        for (Object jaxbNode : jaxbNodes){
            Segment paragraph = new Segment();
            paragraph.setText(jaxbNode.toString());
            paragraph.setDocument(document);
            segmentRepository.save(paragraph);
            P p = ((P) XmlUtils.unwrap(jaxbNode));
            if(p != null) {
                for(Object obj : p.getContent()) {
                    R r = ((R) XmlUtils.unwrap(obj));
                    Segment run = new Segment();
                    StringWriter w = new StringWriter();
                    TextUtils.extractText(r, w);
                    run.setText(w.toString());
                    run.setBold(r.getRPr().getB() != null);
                    run.setItalic(r.getRPr().getI() != null);
                    run.setStrike(r.getRPr().getStrike() != null);
                    U u = r.getRPr().getU();
                    run.setUnderlineEnumeration(u != null ? u.getVal() : null);
                    run.setDocument(document);
                    run.setParagraph(paragraph);
                    segmentRepository.save(run);
                }
            }
        }
    }
}