package com.example.martyrs.service;

import com.example.martyrs.dto.StatisticsVO;
import com.example.martyrs.entity.*;
import com.example.martyrs.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MartyrService {

    private final MartyrBasicInfoRepository martyrRepo;
    private final MartyrDeedRepository deedRepo;
    private final MediaLibraryRepository mediaRepo;
    private final CulturalRelicRepository relicRepo;
    private final HonorMemorialRepository honorRepo;

    public MartyrService(MartyrBasicInfoRepository martyrRepo, MartyrDeedRepository deedRepo,
                         MediaLibraryRepository mediaRepo, CulturalRelicRepository relicRepo,
                         HonorMemorialRepository honorRepo) {
        this.martyrRepo = martyrRepo;
        this.deedRepo = deedRepo;
        this.mediaRepo = mediaRepo;
        this.relicRepo = relicRepo;
        this.honorRepo = honorRepo;
    }

    // ==================== Martyr Basic Info ====================
    public Page<MartyrBasicInfo> getMartyrPage(int page, int size, String keyword) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (keyword != null && !keyword.isEmpty()) {
            return martyrRepo.search(keyword, pr);
        }
        return martyrRepo.findByDeletedAtIsNull(pr);
    }

    public MartyrBasicInfo getMartyrById(Long id) {
        return martyrRepo.findById(id).orElseThrow(() -> new RuntimeException("烈士信息不存在"));
    }

    public MartyrBasicInfo saveMartyr(MartyrBasicInfo martyr) {
        return martyrRepo.save(martyr);
    }

    @Transactional
    public void deleteMartyr(Long id) {
        MartyrBasicInfo martyr = getMartyrById(id);
        martyr.setDeletedAt(LocalDateTime.now());
        martyr.setStatus(0);
        martyrRepo.save(martyr);

        deedRepo.findByMartyrIdAndDeletedAtIsNull(id).forEach(d -> {
            d.setDeletedAt(LocalDateTime.now());
            d.setStatus(0);
            deedRepo.save(d);
        });
        mediaRepo.findByOwnerTypeAndOwnerIdAndDeletedAtIsNull("MARTYR", id).forEach(m -> {
            m.setDeletedAt(LocalDateTime.now());
            m.setStatus(0);
            mediaRepo.save(m);
        });
        relicRepo.findByMartyrIdAndDeletedAtIsNull(id).forEach(r -> {
            r.setDeletedAt(LocalDateTime.now());
            r.setStatus(0);
            relicRepo.save(r);
        });
        honorRepo.findByMartyrIdAndDeletedAtIsNull(id).forEach(h -> {
            h.setDeletedAt(LocalDateTime.now());
            h.setStatus(0);
            honorRepo.save(h);
        });
    }

    // ==================== Martyr Deed ====================
    public Page<MartyrDeed> getDeedPage(int page, int size, String keyword) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (keyword != null && !keyword.isEmpty()) {
            return deedRepo.search(keyword, pr);
        }
        return deedRepo.findByDeletedAtIsNull(pr);
    }

    public List<MartyrDeed> getDeedsByMartyrId(Long martyrId) {
        return deedRepo.findByMartyrIdAndDeletedAtIsNull(martyrId);
    }

    public MartyrDeed saveDeed(MartyrDeed deed) {
        return deedRepo.save(deed);
    }

    public void deleteDeed(Long id) {
        MartyrDeed deed = deedRepo.findById(id).orElseThrow(() -> new RuntimeException("事迹不存在"));
        deed.setDeletedAt(LocalDateTime.now());
        deed.setStatus(0);
        deedRepo.save(deed);
    }

    // ==================== Media Library ====================
    public Page<MediaLibrary> getMediaPage(int page, int size, String keyword) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (keyword != null && !keyword.isEmpty()) {
            return mediaRepo.search(keyword, pr);
        }
        return mediaRepo.findByDeletedAtIsNull(pr);
    }

    public List<MediaLibrary> getMediaByOwner(String ownerType, Long ownerId) {
        return mediaRepo.findByOwnerTypeAndOwnerIdAndDeletedAtIsNullOrderByUploadDateAsc(ownerType, ownerId);
    }

    public List<MediaLibrary> getMediaByMartyrId(Long martyrId) {
        return getMediaByOwner("MARTYR", martyrId);
    }

    public MediaLibrary saveMedia(MediaLibrary media) {
        return mediaRepo.save(media);
    }

    public void deleteMedia(Long id) {
        MediaLibrary media = mediaRepo.findById(id).orElseThrow(() -> new RuntimeException("媒体资源不存在"));
        media.setDeletedAt(LocalDateTime.now());
        media.setStatus(0);
        mediaRepo.save(media);
    }

    // ==================== Avatar ====================
    @Transactional
    public void setAvatar(Long mediaId) {
        MediaLibrary media = mediaRepo.findById(mediaId).orElseThrow(() -> new RuntimeException("媒体资源不存在"));
        if (!"MARTYR".equals(media.getOwnerType()) || media.getOwnerId() == null) {
            throw new RuntimeException("只有属于烈士的图片才能设为头像");
        }
        media.setIsAvatar(true);
        mediaRepo.save(media);

        MartyrBasicInfo martyr = martyrRepo.findById(media.getOwnerId())
                .orElseThrow(() -> new RuntimeException("烈士不存在"));
        martyr.setPhoto(media.getFilePath());
        martyrRepo.save(martyr);
    }

    @Transactional
    public void clearAvatar(Long mediaId) {
        MediaLibrary media = mediaRepo.findById(mediaId).orElseThrow(() -> new RuntimeException("媒体资源不存在"));
        media.setIsAvatar(false);
        mediaRepo.save(media);

        if ("MARTYR".equals(media.getOwnerType()) && media.getOwnerId() != null) {
            MartyrBasicInfo martyr = martyrRepo.findById(media.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("烈士不存在"));
            martyr.setPhoto(null);
            martyrRepo.save(martyr);
        }
    }

    // ==================== Cultural Relic ====================
    public Page<CulturalRelic> getRelicPage(int page, int size, String keyword) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (keyword != null && !keyword.isEmpty()) {
            return relicRepo.search(keyword, pr);
        }
        return relicRepo.findByDeletedAtIsNull(pr);
    }

    public List<CulturalRelic> getRelicsByMartyrId(Long martyrId) {
        return relicRepo.findByMartyrIdAndDeletedAtIsNull(martyrId);
    }

    public CulturalRelic saveRelic(CulturalRelic relic) {
        return relicRepo.save(relic);
    }

    public void deleteRelic(Long id) {
        CulturalRelic relic = relicRepo.findById(id).orElseThrow(() -> new RuntimeException("文物不存在"));
        relic.setDeletedAt(LocalDateTime.now());
        relic.setStatus(0);
        relicRepo.save(relic);
    }

    // ==================== Honor Memorial ====================
    public Page<HonorMemorial> getHonorPage(int page, int size, String keyword) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (keyword != null && !keyword.isEmpty()) {
            return honorRepo.search(keyword, pr);
        }
        return honorRepo.findByDeletedAtIsNull(pr);
    }

    public List<HonorMemorial> getHonorsByMartyrId(Long martyrId) {
        return honorRepo.findByMartyrIdAndDeletedAtIsNull(martyrId);
    }

    public HonorMemorial saveHonor(HonorMemorial honor) {
        return honorRepo.save(honor);
    }

    public void deleteHonor(Long id) {
        HonorMemorial honor = honorRepo.findById(id).orElseThrow(() -> new RuntimeException("荣誉不存在"));
        honor.setDeletedAt(LocalDateTime.now());
        honor.setStatus(0);
        honorRepo.save(honor);
    }

    // ==================== Statistics ====================
    public StatisticsVO getStatistics() {
        StatisticsVO vo = new StatisticsVO();
        vo.setMartyrCount(martyrRepo.countByDeletedAtIsNull());
        vo.setDeedCount(deedRepo.countByDeletedAtIsNull());
        vo.setMediaCount(mediaRepo.countByDeletedAtIsNull());
        vo.setRelicCount(relicRepo.countByDeletedAtIsNull());
        vo.setHonorCount(honorRepo.countByDeletedAtIsNull());
        vo.setTotalCount(vo.getMartyrCount() + vo.getDeedCount() + vo.getMediaCount()
                + vo.getRelicCount() + vo.getHonorCount());
        return vo;
    }

    // ==================== Recycle Bin ====================
    public List<MartyrBasicInfo> getDeletedMartyrs() {
        return martyrRepo.findByDeletedAtIsNotNull();
    }

    @Transactional
    public void restoreMartyr(Long id) {
        MartyrBasicInfo martyr = martyrRepo.findById(id).orElseThrow(() -> new RuntimeException("不存在"));
        martyr.setDeletedAt(null);
        martyr.setStatus(1);
        martyrRepo.save(martyr);
    }

    @Transactional
    public void permanentlyDelete(Long id) {
        martyrRepo.deleteById(id);
    }

    // ==================== Search All ====================
    public List<?> searchAll(String keyword) {
        return martyrRepo.search(keyword, PageRequest.of(0, 20)).getContent();
    }
}
