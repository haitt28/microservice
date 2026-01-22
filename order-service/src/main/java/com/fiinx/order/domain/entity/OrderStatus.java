package com.fiinx.order.domain.entity;

/**
 * Order Status with allowed transitions
 * 
 * State Machine:
 * CREATED → PENDING_INVENTORY → INVENTORY_RESERVED → PENDING_PAYMENT → PAYMENT_PROCESSED → COMPLETED
 *                ↓                      ↓                    ↓
 *              FAILED               FAILED                FAILED
 *              
 * Any state → CANCELLED (if not already COMPLETED)
 */
public enum OrderStatus {
    
    /**
     * Order just created, not yet processed
     */
    CREATED {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == PENDING_INVENTORY || 
                   newStatus == CANCELLED || 
                   newStatus == FAILED;
        }
    },
    
    /**
     * Waiting for inventory reservation
     */
    PENDING_INVENTORY {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == INVENTORY_RESERVED || 
                   newStatus == CANCELLED || 
                   newStatus == FAILED;
        }
    },
    
    /**
     * Inventory has been reserved
     */
    INVENTORY_RESERVED {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == PENDING_PAYMENT || 
                   newStatus == CANCELLED || 
                   newStatus == FAILED;
        }
    },
    
    /**
     * Waiting for payment processing
     */
    PENDING_PAYMENT {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == PAYMENT_PROCESSED || 
                   newStatus == CANCELLED || 
                   newStatus == FAILED;
        }
    },
    
    /**
     * Payment has been processed
     */
    PAYMENT_PROCESSED {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == COMPLETED || 
                   newStatus == FAILED;
        }
    },
    
    /**
     * Order completed successfully
     */
    COMPLETED {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return false; // Trạng thái cuối cùng (Terminal state)
        }
    },
    
    /**
     * Order failed
     */
    FAILED {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return false; // Trạng thái cuối cùng (Terminal state)
        }
    },
    
    /**
     * Order cancelled
     */
    CANCELLED {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return false; // Trạng thái cuối cùng (Terminal state)
        }
    };
    
    /**
     * Check if transition to new status is allowed
     */
    public abstract boolean canTransitionTo(OrderStatus newStatus);
    
    /**
     * Check if this is a terminal state
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
    
    /**
     * Check if order is still in progress
     */
    public boolean isInProgress() {
        return this == CREATED || 
               this == PENDING_INVENTORY || 
               this == INVENTORY_RESERVED || 
               this == PENDING_PAYMENT ||
               this == PAYMENT_PROCESSED;
    }
}
