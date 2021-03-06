package net.okjsp



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class BannerController {

    UserService userService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @Transactional
    def stats(Banner banner) {

        if (banner == null) {
            notFound()
            return
        }

        String ip = userService.getRealIp(request)

        def bannerClick = BannerClick.findOrCreateWhere(banner: banner, ip: ip)

        bannerClick.clickCount++
        bannerClick.save(flush: true)

        redirect url: banner.url
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Banner.list(params), model:[bannerCount: Banner.count()]
    }

    def show(Banner banner) {
        respond banner
    }

    def create() {
        respond new Banner(params)
    }

    @Transactional
    def save(Banner banner) {
        if (banner == null) {
            notFound()
            return
        }

        if (banner.hasErrors()) {
            respond banner.errors, view:'create'
            return
        }

        banner.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'banner.label', default: 'Banner'), banner.id])
                redirect banner
            }
            '*' { respond banner, [status: CREATED] }
        }
    }

    def edit(Banner banner) {
        respond banner
    }

    @Transactional
    def update(Banner banner) {
        if (banner == null) {
            notFound()
            return
        }

        if (banner.hasErrors()) {
            respond banner.errors, view:'edit'
            return
        }

        banner.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Banner.label', default: 'Banner'), banner.id])
                redirect banner
            }
            '*'{ respond banner, [status: OK] }
        }
    }

    @Transactional
    def delete(Banner banner) {

        if (banner == null) {
            notFound()
            return
        }

        banner.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Banner.label', default: 'Banner'), banner.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'banner.label', default: 'Banner'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
