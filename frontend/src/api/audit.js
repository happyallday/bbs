import request from './request'

export function getPendingAudits(params) {
  return request({
    url: '/admin/audit/pending',
    method: 'get',
    params
  })
}

export function getAuditHistory(userId, params) {
  return request({
    url: '/admin/audit/history',
    method: 'get',
    params: {
      ...params,
      userId
    }
  })
}

export function approveAudit(auditLogId, remark) {
  return request({
    url: `/admin/audit/approve/${auditLogId}`,
    method: 'post',
    data: { remark }
  })
}

export function rejectAudit(auditLogId, remark) {
  return request({
    url: `/admin/audit/reject/${auditLogId}`,
    method: 'post',
    data: { remark }
  })
}

export function getAuditStatistics() {
  return request({
    url: '/admin/audit/statistics',
    method: 'get'
  })
}

export function checkBeforeSubmit(content) {
  return request({
    url: '/admin/audit/check',
    method: 'post',
    data: { content }
  })
}