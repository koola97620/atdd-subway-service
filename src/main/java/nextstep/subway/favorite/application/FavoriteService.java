package nextstep.subway.favorite.application;

import nextstep.subway.favorite.domain.Favorite;
import nextstep.subway.favorite.domain.FavoriteRepository;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;
import nextstep.subway.member.application.MemberService;
import nextstep.subway.member.domain.Member;
import nextstep.subway.path.exception.SameStartEndStationException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final MemberService memberService;
    private final StationService stationService;
    private final FavoriteRepository favoriteRepository;

    public FavoriteService(MemberService memberService, StationService stationService, FavoriteRepository favoriteRepository) {
        this.memberService = memberService;
        this.stationService = stationService;
        this.favoriteRepository = favoriteRepository;
    }

    public Favorite createFavorite(Long id, FavoriteRequest favoriteRequest) {
        isValidRequest(favoriteRequest);

        Member member = memberService.findMemberById(id);
        Station source = stationService.findStationById(favoriteRequest.getSource());
        Station target = stationService.findStationById(favoriteRequest.getTarget());
        Favorite favorite = Favorite.of(member, source, target);
        return favoriteRepository.save(favorite);
    }

    private void isValidRequest(FavoriteRequest favoriteRequest) {
        if (favoriteRequest.isSameSourceAndTarget()) {
            throw new SameStartEndStationException();
        }
    }

    public List<FavoriteResponse> findAllFavorites(Long id) {
        Member member = memberService.findMemberById(id);
        List<Favorite> favorites = favoriteRepository.findAllByMember(member);
        return favorites.stream()
                .map(FavoriteResponse::of)
                .collect(Collectors.toList());
    }

    public void deleteFavorite(Long favoriteId) {
        favoriteRepository.deleteById(favoriteId);
    }
}
